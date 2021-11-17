package bios.schedulers;

import bios.Kernel;
import bios.Process;

import java.util.Comparator;

public class ProcessComparator implements Comparator<Process> {
    private final Kernel kernel;

    public ProcessComparator(Kernel kernel) {
        this.kernel = kernel;
    }

    public int compare(Process process1, Process process2) {
        long process1Age = kernel.cpu.getCycles() - process1.getLastBurst();
        long process2Age = kernel.cpu.getCycles() - process2.getLastBurst();

        process1.setWeightedPriority(process1.getPriority() + (process1Age/50));
        process2.setWeightedPriority(process2.getPriority() + (process2Age/50));

        if (process1.getWeightedPriority() < process2.getWeightedPriority()) { return 1; }
        else if (process1.getWeightedPriority() > process2.getWeightedPriority()) { return -1; }
        else if (process1.getProcessID() > process2.getProcessID()) { return 1; }
        else if (process1.getProcessID() < process2.getProcessID()) { return -1; }

        return 0;
    }
}
