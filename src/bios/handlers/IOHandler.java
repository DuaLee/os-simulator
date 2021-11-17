package bios.handlers;

import bios.IODeviceIdentifier;
import bios.Kernel;
import bios.Lock;
import bios.Process;
import cpu.CPU;
import bios.schedulers.*;

import java.util.PriorityQueue;

public class IOHandler {
    private final Kernel kernel;
    private final CPU cpu;
    public final Lock[] locks;
    public final IODeviceIdentifier ioDeviceIdentifier = new IODeviceIdentifier();

    public IOHandler(Kernel kernel) {
        this.kernel = kernel;
        this.cpu = kernel.cpu;
        this.locks = new Lock[ioDeviceIdentifier.getNumDevices()];

        for(int i = 0; i < ioDeviceIdentifier.getNumDevices(); i++) {
            this.locks[i] = new Lock();
        }

    }

    public void ioIn() {
        Process pointer = this.cpu.pointer;

        if (this.locks[pointer.getIoDevice()].ioIn()) {
            System.out.println("IO device " + ioDeviceIdentifier.getDeviceName(pointer.getIoDevice()) + " [" + pointer.getIoDevice() + "] in: Process " + pointer.getProcessID());

            this.cpu.instructionCounter = 0;
        } else {
            System.out.println("IO device " + ioDeviceIdentifier.getDeviceName(pointer.getIoDevice()) + " [" + pointer.getIoDevice() + "] wait: Process " + this.cpu.pointer.getProcessID());

            pointer.state = ProcessState.IO_REQUEST;
            Process nextWaiting = this.kernel.shortScheduler.getNextProcess();
            Process oldProcess;

            if (nextWaiting == null) {
                nextWaiting = this.kernel.shortScheduler.getIoWaitQueue().poll();
                if (nextWaiting == null) {
                    System.out.println("---IO PROCESS NOT FOUND ERROR---");
                }

                oldProcess = this.kernel.switchHandler.handleSwitch(nextWaiting);

                this.kernel.shortScheduler.addProcess(oldProcess);
                this.kernel.ioWaitHandler.waitState();
            } else {
                oldProcess = this.kernel.switchHandler.handleSwitch(nextWaiting);

                this.kernel.shortScheduler.addProcess(oldProcess);
            }
        }

    }

    public void ioOut() {
        Process pointer = this.cpu.pointer;

        System.out.println("IO device " + ioDeviceIdentifier.getDeviceName(pointer.getIoDevice()) + " [" + pointer.getIoDevice() + "] out: Process " + pointer.getProcessID());

        int deviceNumber = pointer.getIoDevice();
        PriorityQueue<Process> deviceQueue = this.kernel.shortScheduler.getIoQueue(deviceNumber);

        if (!deviceQueue.isEmpty()) {
            Process nextInLine = deviceQueue.poll();

            System.out.println("IO in after wait: Process " + nextInLine.getProcessID());
            nextInLine.state = ProcessState.READY;
            this.kernel.shortScheduler.addProcess(nextInLine);
        }

        this.locks[deviceNumber].ioOut();
    }
}
