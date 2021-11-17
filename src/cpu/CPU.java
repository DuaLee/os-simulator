package cpu;

import bios.Kernel;
import bios.Process;
import bios.schedulers.ProcessState;

import java.util.ArrayList;
import java.util.LinkedList;

public class CPU {
    private static final int DEFAULT_MEMORY_SIZE = 2048; //kB
    public static final int DEFAULT_NUM_THREADS = 2;

    public int memorySize;
    public Thread[] threads;

    public Kernel kernel;

    public final ProcessorInterrupts processorInterrupts;
    public boolean blocked;
    public final IO io;

    public long cycles;

    public Process pointer; //simulated pointer block for current process
    public int interruptTimer;
    public int programCounter;
    public int instructionCounter;

    public CPU() { this(DEFAULT_MEMORY_SIZE, DEFAULT_NUM_THREADS); }

    public CPU(int memorySize, int numThreads) {
        this.memorySize = memorySize;
        threads = new Thread[numThreads];

        processorInterrupts = new ProcessorInterrupts();
        this.blocked = false;

        io = new IO(this);

        cycles = 0;
    }

    public boolean isReadyForCycle() {
        if (pointer == null) {
            this.pointer = kernel.shortScheduler.getNextProcess();

            if (pointer == null)
                return false;

            this.pointer.state = ProcessState.EXECUTING;
            this.programCounter = -1;
            this.instructionCounter = 0;
        }

        if(threads.length > 1) {
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(this::cycle);
            }

            for (Thread thread : threads) {
                thread.start();
            }
        }
        else
            cycle();

        return true;
    }

    private void cycle() {
        ++cycles;
        io.cycle();

        if (processorInterrupts.isInterruptPending()) {
            processorInterrupts.interrupt();
        } else if (blocked){
            return;
        } else if (interruptTimer <= 0 ) {
            processorInterrupts.setInterruptFlag(ProcessorInterrupts.YIELD);
            processorInterrupts.interrupt();
        } else {
            --interruptTimer;

            if (instructionCounter == 0) {
                ++programCounter;
                loadAssembly();
            }

            runAssembly();
            pointer.setCpuCyclesUsed(1 + pointer.getCpuCyclesUsed());
        }

        System.out.println("CPU cycle: " + cycles + " | RAM usage: " + kernel.longScheduler.getMemoryUsage() + "kB | VRAM usage: " + kernel.longScheduler.getSwappedUsage() + "kB | Current process: " + pointer.getProcessName() + " [" + pointer.getProcessID() + "]");
    }

    private void loadAssembly() {
        Assembly assembly = pointer.program.get(programCounter);

        switch(assembly.getInstruction()){
            case Assembly.CALCULATE:
                instructionCounter = assembly.getCpuCost();
                break;

            case Assembly.SEM_IN:
                instructionCounter = 1;
                break;

            case Assembly.IO:
            case Assembly.SEM_OUT:
            case Assembly.END:
                instructionCounter = 0;
                break;

        }
    }

    private void runAssembly() {
        Assembly assembly = pointer.program.get(programCounter);

        switch(assembly.getInstruction()){
            case Assembly.CALCULATE:
                --instructionCounter;
                break;

            case Assembly.IO:
                io.runIO();
                processorInterrupts.setInterruptFlag(ProcessorInterrupts.IO_WAIT);
                break;

            case Assembly.SEM_IN:
                processorInterrupts.setInterruptFlag(ProcessorInterrupts.SEM_IN);
                break;

            case Assembly.SEM_OUT:
                processorInterrupts.setInterruptFlag(ProcessorInterrupts.SEM_OUT);
                break;

            case Assembly.END:
                System.out.println("Process " + pointer.PROCESS_ID + " terminated");

                pointer.state = ProcessState.TERMINATED;
                processorInterrupts.setInterruptFlag(ProcessorInterrupts.TERMINATE);
                break;
        }
    }

    public long getCycles() { return cycles; }
    public int getMemorySize() { return memorySize; }
}