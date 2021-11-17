package bios.handlers;

import bios.Kernel;
import bios.Process;

public class YieldHandler {
    private final Kernel kernel;

    public YieldHandler(Kernel kernel) { this.kernel = kernel; }

    public void handleInterrupt() {
        Process nextInLine = this.kernel.shortScheduler.getNextProcess();

        if (nextInLine != null) {
            Process oldProcess = this.kernel.switchHandler.handleSwitch(nextInLine);

            this.kernel.shortScheduler.addProcess(oldProcess);
        } else {
            this.kernel.cpu.interruptTimer = this.kernel.shortScheduler.getShortTimeLimit();
        }

    }
}
