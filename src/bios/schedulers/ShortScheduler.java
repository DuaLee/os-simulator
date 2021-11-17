package bios.schedulers;

import bios.Kernel;
import bios.Process;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class ShortScheduler {
    private final Kernel kernel;

    private final PriorityQueue<Process> readyQueue;
    private final LinkedList<Process> ioWaitQueue;
    private final PriorityQueue<Process>[] ioQueue;

    public static final int LONG_TIME_LIMIT = 25;
    public static final int SHORT_TIME_LIMIT = 5;
    private int longSchedulerTimer = 0;

    public ShortScheduler(Kernel kernel) {
        this.kernel = kernel;

        this.readyQueue = new PriorityQueue(8, new ProcessComparator(kernel));
        this.ioWaitQueue = new LinkedList<>();
        this.ioQueue = new PriorityQueue[Kernel.IO_COUNT];

        for (int i = 0; i < Kernel.IO_COUNT; i++) {
            ioQueue[i] = new PriorityQueue(8, new ProcessComparator(kernel));
        }
    }

    public void addProcess(Process process) {
        if (process.getState() == ProcessState.READY) {
            readyQueue.add(process);
        } else if (process.getState() == ProcessState.IO_REQUEST) {
            ioQueue[process.getIoDevice()].add(process); //per device io queue
        } else if (process.getState() == ProcessState.IO_WAIT) {
            ioWaitQueue.add(process);
        } else
            System.out.println("---SCHEDULER ERROR---");
    }

    public Process getNextProcess() {
        if (longSchedulerTimer == 0 || readyQueue.isEmpty()) {
            kernel.longScheduler.schedule();
            longSchedulerTimer = LONG_TIME_LIMIT;
        } else {
            longSchedulerTimer--;
        }

        return readyQueue.poll();
    }

    public Process peekTarget() {
        if (readyQueue.isEmpty()) {
            return null;
        } else {
            Iterator<Process> iter = readyQueue.iterator();
            Process last = null;

            while(iter.hasNext()) {
                last = iter.next();
            }

            return last;
        }
    }

    public PriorityQueue<Process> getReadyQueue() { return readyQueue; }

    public LinkedList<Process> getIoWaitQueue() { return ioWaitQueue; }

    public PriorityQueue<Process> getIoQueue(int ioIndex) { return ioQueue[ioIndex]; }

    public int getShortTimeLimit() { return SHORT_TIME_LIMIT; }
}
