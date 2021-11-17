package bios.handlers;

import bios.Kernel;
import bios.Process;
import bios.schedulers.ProcessState;

public class IOWaitHandler {
    private final Kernel kernel;
    private boolean waiting;
    private boolean blocking;

    public IOWaitHandler(Kernel kernel) {
        this.kernel = kernel;
    }

    public void handleBlocking() {
        this.blocking = true;
        this.kernel.cpu.pointer.state = ProcessState.IO_WAIT;
        Process nextWaiting = this.kernel.shortScheduler.getNextProcess();

        if (nextWaiting != null) {
            this.waiting = false;
            Process oldPCB = this.kernel.switchHandler.handleSwitch(nextWaiting);

            if (!this.kernel.shortScheduler.getIoWaitQueue().isEmpty()) {
                System.out.println("---IO BLOCKED---");
            }

            this.kernel.shortScheduler.getIoWaitQueue().add(oldPCB);
        } else {
            this.waitState();
        }

    }

    public void waitState() {
        if (this.blocking) {
            this.waiting = true;
            this.kernel.cpu.interruptTimer = this.kernel.shortScheduler.getShortTimeLimit();
            this.kernel.cpu.blocked = true;
        } else {
            System.out.println("---IO WAIT ERROR---");
        }

    }

    public void signalIOCompletion() {
        this.blocking = false;

        if (this.waiting) {
            this.kernel.cpu.blocked = false;
            this.kernel.cpu.pointer.state = ProcessState.EXECUTING;
        } else {
            Process waitingProcess = (Process)this.kernel.shortScheduler.getIoWaitQueue().remove();
            Process oldProcess = this.kernel.switchHandler.handleSwitch(waitingProcess);

            this.kernel.shortScheduler.addProcess(oldProcess);
        }

    }

    public boolean isBlocking() { return this.blocking; }
}