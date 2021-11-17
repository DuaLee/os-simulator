package bios;

import java.util.ArrayList;

import bios.schedulers.ProcessState;
import cpu.Assembly;

/**
 * Fields represent elements of the Process Control Block
 */
public class Process {
    public final int PROCESS_ID;
    public final Process CHILD_PROCESS;
    public final String PROCESS_NAME;
    public final int MEMORY_LIMIT;
    public final long START_TIME;
    public final int PRIORITY;
    public final long MAX_CPU_CYCLES;

    public ArrayList<Assembly> program;
    public ProcessState state;

    public int programCounter;
    public int instructionCounter;
    public long cpuCyclesUsed;

    public long lastBurst;
    public long weightedPriority;

    public int ioDevice = 0;

    //Process with child
    public Process(int PROCESS_ID, Process CHILD_PROCESS, String PROCESS_NAME, int MEMORY_LIMIT, int PRIORITY,
                   long START_TIME, long MAX_CPU_CYCLES, ArrayList<Assembly> program) {
        this.PROCESS_ID = PROCESS_ID;
        this.CHILD_PROCESS = CHILD_PROCESS;
        this.PROCESS_NAME = PROCESS_NAME;
        this.MEMORY_LIMIT = MEMORY_LIMIT;
        this.START_TIME = START_TIME;
        this.PRIORITY = PRIORITY;
        this.MAX_CPU_CYCLES = MAX_CPU_CYCLES;

        this.program = program;
        this.state = ProcessState.NEW;

        this.programCounter = -1;
        this.instructionCounter = 0;
        this.cpuCyclesUsed = 0;

        this.lastBurst = 0;

        for (Assembly assembly : program) {
            if (assembly.getIoDevice() > 0)
                ioDevice = assembly.getIoDevice();
        }
        //for (Assembly a : program) System.out.println(a.getInstruction() + " " + a.getCpuCost() + " " + a.getIoDevice());
    }

    /**
     * Getters and setters for PCB fields
     */
    public int getProcessID() { return PROCESS_ID; }
    public Process getChildProcess() { return CHILD_PROCESS; }
    public String getProcessName() { return PROCESS_NAME; }
    public int getMemoryLimit() { return MEMORY_LIMIT; }
    public int getPriority() { return PRIORITY; }

    public ProcessState getState() { return state; }
    public void setState(ProcessState state) { this.state = state; }

    public int getProgramCounter() { return programCounter; }
    public void setProgramCounter(int programCounter) { this.programCounter = programCounter; }

    public int getInstructionCounter() { return instructionCounter; }
    public void setInstructionCounter(int instructionCounter) { this.instructionCounter = instructionCounter; }

    public long getCpuCyclesUsed() { return cpuCyclesUsed; }
    public void setCpuCyclesUsed(long cpuCyclesUsed) { this.cpuCyclesUsed = cpuCyclesUsed; }

    public long getLastBurst() { return lastBurst; }
    public void setLastBurst(long lastBurst) { this.lastBurst = lastBurst; }

    public long getWeightedPriority() {
        return weightedPriority;
    }
    public void setWeightedPriority(long weightedPriority) {
        this.weightedPriority = weightedPriority;
    }

    public Integer getIoDevice() { return ioDevice; }

    //for piping process information to GUI
    public String toString() {
        return PROCESS_ID + "%" + PROCESS_NAME + "%" + state + "%" + cpuCyclesUsed + "%" + MEMORY_LIMIT + "%" + PRIORITY + "%" + weightedPriority;
    }
}
