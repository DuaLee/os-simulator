package cpu;

import bios.IODeviceIdentifier;

import java.util.Random;

public class IO {
    private static final int MAX_IO_WAIT_TIME = 25; //max is actually 30 in practice

    public final CPU cpu;

    public final IODeviceIdentifier ioDeviceIdentifier = new IODeviceIdentifier();

    private Random random = new Random();

    //Simulates physical input and output devices
    private long ioTime;
    private boolean ioActive;

    public IO(CPU cpu) {
        this.cpu = cpu;
        ioActive = false;
    }

    public void runIO() {
        ioActive = true;
        ioTime = cpu.getCycles() + random.nextInt(MAX_IO_WAIT_TIME) + 5;
    }

    public void cycle() {
        if (ioActive && cpu.getCycles() >= ioTime) {
            cpu.processorInterrupts.setInterruptFlag(ProcessorInterrupts.IO_COMPLETE);
            ioActive = false;
        }

        if (isIoActive())
            System.out.println("IO device " + ioDeviceIdentifier.getDeviceName(cpu.pointer.getIoDevice()) + " [" + cpu.pointer.getIoDevice() + "] time cycle: " + cpu.cycles);
    }

    public boolean isIoActive() { return ioActive; }
}
