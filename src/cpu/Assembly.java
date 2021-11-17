package cpu;

public class Assembly {
    public static final int END = 0, CALCULATE = 1, IO = 2, SEM_IN = 3, SEM_OUT = 4, YIELD = 5, FORK = 6;

    private final int instruction, cpuCost, ioDevice;

    //no IO device
    public Assembly(int instruction, int cpuCost) {
        this.instruction = instruction;
        this.cpuCost = cpuCost;
        this.ioDevice = 0;
    }

    //IO device parameter
    public Assembly(int instruction, int cpuCost, int ioDevice) {
        this.instruction = instruction;
        this.cpuCost = cpuCost;
        this.ioDevice = ioDevice;
    }

    public int getInstruction() { return instruction; }
    public int getCpuCost() { return cpuCost; }
    public int getIoDevice() { return ioDevice; }
}