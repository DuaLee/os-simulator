package cpu;

import bios.handlers.*;

public class ProcessorInterrupts {
    public static final int INTERRUPT_COUNT = 6;

    public static final int TERMINATE = 0, SEM_IN = 1, SEM_OUT = 2, IO_WAIT = 3, IO_COMPLETE = 4, YIELD = 5;

    private final boolean [] interruptFlags = new boolean[INTERRUPT_COUNT];

    public TerminateHandler terminateHandler;
    public IOHandler ioHandler;
    public IOWaitHandler ioWaitHandler;
    public YieldHandler yieldHandler;

    public ProcessorInterrupts() {
        for (int i = 0; i < INTERRUPT_COUNT; i++) {
            interruptFlags[i] = false;
        }
    }

    public boolean isInterruptPending() {
        boolean pending = false;
        for (int i = 0; i < INTERRUPT_COUNT; i++) {
            pending = pending || interruptFlags[i];
        }
        return pending;
    }

    public void interrupt() {
        if (this.interruptFlags[0]) {
            this.interruptFlags[0] = false;
            this.terminateHandler.handleInterrupt();
        } else if (this.interruptFlags[1]) {
            this.interruptFlags[1] = false;
            this.ioHandler.ioIn();
        } else if (this.interruptFlags[2]) {
            this.interruptFlags[2] = false;
            this.ioHandler.ioOut();
        } else if (this.interruptFlags[3]) {
            this.interruptFlags[3] = false;
            this.ioWaitHandler.handleBlocking();
        } else if (this.interruptFlags[4]) {
            this.interruptFlags[4] = false;
            this.ioWaitHandler.signalIOCompletion();
        } else if (this.interruptFlags[5]) {
            this.interruptFlags[5] = false;
            this.yieldHandler.handleInterrupt();
        }

    }

    public void setInterruptFlag(int flag) { interruptFlags[flag] = true; }
}