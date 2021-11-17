package bios.schedulers;

import java.util.LinkedList;
import java.util.PriorityQueue;

import bios.Kernel;
import bios.Process;
import cpu.Assembly;
import cpu.CPU;

public class LongScheduler {
    private final Kernel kernel;
    private final CPU cpu;

    private final ShortScheduler shortScheduler;
    private final ProcessComparator processComparator;

    private final LinkedList<Process> newQueue;
    private final PriorityQueue<Process> waitingQueue;

    public LongScheduler(Kernel kernel) {
        this.kernel = kernel;
        this.cpu = kernel.cpu;

        this.shortScheduler = kernel.shortScheduler;
        this.processComparator = new ProcessComparator(kernel);

        this.newQueue = new LinkedList<>();
        this.waitingQueue = new PriorityQueue(8, processComparator);
    }

    public void addProcess(Process process) {
        newQueue.add(process);
    }

    public void schedule() {
        while (!newQueue.isEmpty()) {
            Process newProcess = newQueue.poll();

            swapOut(newProcess);
        }

        trySwapIn();

        //Process bound swap

        Process nextWaiting = waitingQueue.peek();
        Process target = shortScheduler.peekTarget();

        while (nextWaiting != null && target != null && processComparator.compare(target, nextWaiting) > 0) {
            ioSwapIn(waitingQueue.poll());

            nextWaiting = waitingQueue.peek();
            target = shortScheduler.peekTarget();
        }
    }

    //Memory bound swap

    private void trySwapIn() {
        Process nextWaiting = waitingQueue.peek();

        if (nextWaiting != null && nextWaiting.getMemoryLimit() > cpu.getMemorySize()) {
            System.out.println("---INSUFFICIENT SYSTEM MEMORY ERROR---");
        }

        while (nextWaiting != null && nextWaiting.getMemoryLimit() + getMemoryUsage() <= cpu.getMemorySize() ) {
            waitingQueue.remove(nextWaiting);
            swapIn(nextWaiting);

            nextWaiting = waitingQueue.peek();
        }
    }

    private void swapIn(Process process) {
        process.setState(ProcessState.READY);
        shortScheduler.addProcess(process);
    }

    private void swapOut(Process process) {
        process.setState(ProcessState.WAITING);
        waitingQueue.add(process);
    }

    private void ioSwapIn(Process process) {
        while(!shortScheduler.getReadyQueue().isEmpty()) {
            Process target = shortScheduler.peekTarget();

            if ( target.programCounter >= 0 && target.program.get(target.programCounter).getInstruction() != Assembly.IO) {
                shortScheduler.getReadyQueue().remove(target);
                swapOut(target);
            }

            if (process.getMemoryLimit() + getMemoryUsage() <= cpu.getMemorySize()) {
                break;
            }
        }

        if (process.getMemoryLimit() + getMemoryUsage() > cpu.getMemorySize()) {
            swapOut(process);

            return;
        }

        swapIn(process);
    }

    public int getMemoryUsage() {
        int memoryUsage = 0;

        if (cpu.pointer != null) {
            memoryUsage += cpu.pointer.getMemoryLimit();
        }

        for (Process process : shortScheduler.getReadyQueue()) {
            memoryUsage += process.getMemoryLimit();
        }

        for (Process process : shortScheduler.getIoWaitQueue()) {
            memoryUsage += process.getMemoryLimit();
        }

        for (int i = 0; i < Kernel.IO_COUNT; i++) {
            for (Process process : shortScheduler.getIoQueue(i)) {
                memoryUsage += process.getMemoryLimit();
            }
        }

        return memoryUsage;
    }

    public int getSwappedUsage() {
        int swappedUsage = 0;

        if (!waitingQueue.isEmpty())
            for (Process process : waitingQueue)
                swappedUsage += process.getMemoryLimit();

        return swappedUsage;
    }

    public PriorityQueue<Process> getWaitingQueue() {
        return waitingQueue;
    }
}