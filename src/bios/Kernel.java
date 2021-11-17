package bios;

import bios.handlers.*;
import bios.schedulers.*;
import cpu.CPU;
import cpu.IO;

import java.util.LinkedList;

public class Kernel {
    public static final IODeviceIdentifier ioDeviceIdentifier = new IODeviceIdentifier();
    public static final int IO_COUNT = ioDeviceIdentifier.getNumDevices();
    public final CPU cpu;

    public final LinkedList<Process> processList;
    public final ShortScheduler shortScheduler;
    public final LongScheduler longScheduler;

    public final SystemCalls systemCalls;

    public final TerminateHandler terminateHandler;
    public final IOHandler ioHandler;
    public final IOWaitHandler ioWaitHandler;
    public final YieldHandler yieldHandler;
    public final SwitchHandler switchHandler;

    public Kernel(CPU cpu) {
        this.cpu = cpu;

        this.processList = new LinkedList<>();
        this.shortScheduler = new ShortScheduler(this);
        this.longScheduler = new LongScheduler(this);

        this.systemCalls = new SystemCalls(this);

        this.terminateHandler = new TerminateHandler(this);
        this.ioHandler = new IOHandler(this);
        this.ioWaitHandler = new IOWaitHandler(this);
        this.yieldHandler = new YieldHandler(this);
        this.switchHandler = new SwitchHandler(this);

        this.cpu.processorInterrupts.terminateHandler = this.terminateHandler;
        this.cpu.processorInterrupts.ioHandler = this.ioHandler;
        this.cpu.processorInterrupts.ioWaitHandler = this.ioWaitHandler;
        this.cpu.processorInterrupts.yieldHandler = this.yieldHandler;

        this.cpu.kernel = this;
    }
}
