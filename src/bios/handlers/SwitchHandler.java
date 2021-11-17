package bios.handlers;

import bios.Kernel;
import bios.Process;
import bios.schedulers.ProcessState;
import cpu.CPU;

public class SwitchHandler {
    private final Kernel kernel;
    private final CPU cpu;

    public SwitchHandler(Kernel kernel) {
        this.kernel = kernel;
        this.cpu = kernel.cpu;
    }

    public Process handleSwitch(Process nextPcb) {
        Process oldProcess = this.cpu.pointer;

        if (oldProcess != null) {
            if (oldProcess.state == ProcessState.EXECUTING) {
                oldProcess.state = ProcessState.READY;
            }

            oldProcess.programCounter = this.cpu.programCounter;
            oldProcess.instructionCounter = this.cpu.instructionCounter;
            oldProcess.lastBurst = this.cpu.getCycles();
        }

        nextPcb.state = ProcessState.EXECUTING;
        this.cpu.pointer = nextPcb;
        this.cpu.programCounter = nextPcb.programCounter;
        this.cpu.instructionCounter = nextPcb.instructionCounter;
        this.cpu.interruptTimer = this.kernel.shortScheduler.getShortTimeLimit();

        return oldProcess;
    }
}
