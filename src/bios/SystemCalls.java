package bios;

import cpu.Assembler;
import cpu.Assembly;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SystemCalls {
    private static final Charset ENCODE_CHARSET = StandardCharsets.UTF_8;
    private final Kernel kernel;

    private int nextProcess = 0;
    private Process child = null;

    public SystemCalls(Kernel kernel) {
        this.kernel = kernel;
    }

    public void queueProcess(String processName, String processText) throws IOException {
        ArrayList<Assembly> assemblyList = Assembler.assemble(processText);
        int memoryLimit = Assembler.memory(processText);
        int priority = Assembler.priority(processText);

        long startTime = kernel.cpu.getCycles();
        long maxCycles = 0;

        boolean hasChild = false;

        for (Assembly assembly : assemblyList) {
            if (assembly.getInstruction() == assembly.FORK) {
                hasChild = true;
            } else if (assembly.getInstruction() == assembly.CALCULATE) {
                maxCycles += assembly.getCpuCost();
            } else {
                ++maxCycles;
            }
        }

        if (hasChild) {
            File currentDir = new File(System.getProperty("user.dir"));
            File softwareDir = new File(currentDir.getAbsolutePath() + "/software");
            File currentFile = new File(softwareDir.getAbsolutePath() + "/Child_Process.os");
            String childProcessText = encodeFile(currentFile);

            child = new Process(++nextProcess, null, processName, memoryLimit, priority, startTime, maxCycles, assemblyList);

            //recursively creates children
            queueProcess("Child_Process.os", childProcessText);

            System.out.println("Child process " + child.getProcessID() + " created");
        }

        Process newProcess = new Process(nextProcess++, child, processName, memoryLimit, priority, startTime, maxCycles, assemblyList);

        kernel.longScheduler.addProcess(newProcess);
        kernel.processList.add(newProcess);
    }

    public String encodeFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

        return new String (encoded, ENCODE_CHARSET);
    }
}
