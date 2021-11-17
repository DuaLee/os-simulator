package bios.handlers;

import bios.Kernel;
import bios.Process;

public class TerminateHandler {
    private final Kernel kernel;

    public TerminateHandler(Kernel kernel) {
        this.kernel = kernel;
    }

    public void handleInterrupt() {
        Process nextWaiting = kernel.shortScheduler.getNextProcess();

        if (nextWaiting == null) {
            nextWaiting = kernel.shortScheduler.getIoWaitQueue().poll();

            if (nextWaiting == null) {
                if (kernel.processList.size() == 1) {
                    kernel.processList.remove(kernel.cpu.pointer);
                    kernel.cpu.pointer = null;

                    System.out.println("All processes terminated");
                } else {
                    System.out.println("---PROCESS LEAK ERROR---");
                    kernel.processList.remove(kernel.cpu.pointer);
                    kernel.cpu.pointer = null;

                    System.out.println("All processes terminated");
                }
            } else {
                Process oldProcess = kernel.switchHandler.handleSwitch(nextWaiting);

                kernel.processList.remove(oldProcess);
                kernel.ioWaitHandler.waitState();
            }
        } else {
            Process oldProcess = kernel.switchHandler.handleSwitch(nextWaiting);

            kernel.processList.remove(oldProcess);
        }

    }
}