package bios;

import cpu.CPU;

public class Bootloader {
    public static Kernel start(CPU cpu) {
        Kernel kernel = new Kernel(cpu);

        return kernel;
    }
}
