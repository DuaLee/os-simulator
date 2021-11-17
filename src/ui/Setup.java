package ui;

import bios.Bootloader;
import bios.Kernel;
import bios.SystemCalls;

import cpu.CPU;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Setup {
    private static final String softwareDirName = "software";
    private static final String programExtension = ".os";

    private static final Charset ENCODE_CHARSET = StandardCharsets.UTF_8;

    private File currentDir, softwareDir;

    private Kernel kernel;
    private SystemCalls systemCalls;

    public CPU cpu;
    public int clockSpeed; //milliseconds
    public int physicalMemory;
    public int numThreads;

    public ArrayList<String> processQueue;

    public boolean running = true;

    public Setup(ArrayList<String> processQueue, int clockSpeed, int physicalMemory, int numThreads, OutputStream outputStream) {
        System.setOut(new PrintStream(outputStream,true));

        this.cpu = new CPU(physicalMemory, numThreads);
        this.kernel = Bootloader.start(cpu);
        this.systemCalls = kernel.systemCalls;

        this.currentDir = new File(System.getProperty("user.dir"));
        this.softwareDir = new File(currentDir.getAbsolutePath() + "/" + softwareDirName);

        this.systemCalls = new SystemCalls(kernel);
        this.clockSpeed = clockSpeed;
        this.physicalMemory = physicalMemory;
        this.numThreads = numThreads;

        this.processQueue = processQueue;
    }

    public void start() throws IOException {
        System.out.println("Setup initialized with " + processQueue.size() + " processes | " + numThreads + " CPU thread(s) started at " + clockSpeed + "ms clock speed | Physical Memory: " + physicalMemory + "kB");

        loadProcess(processQueue);
    }

    public void loadProcess(ArrayList<String> processQueue) throws IOException {
        for (String currentProcess : processQueue) {
            File currentFile;

            if (currentProcess.endsWith(programExtension)) {
                currentFile = new File(softwareDir.getAbsolutePath() + "/" + currentProcess);

                if (currentFile.exists() && !currentFile.isDirectory()) {
                    systemCalls.queueProcess(currentFile.getName(), encodeFile(currentFile));

                    System.out.println("Process \"" + currentProcess + "\" loaded");
                } else
                    System.out.println("Process \"" + currentProcess + "\" not found");
            }
        }

        runCPU();
    }

    public void runCPU() {
        while (running == true) {
            try {
                TimeUnit.MILLISECONDS.sleep(clockSpeed);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();

                break;
            }

            if (cpu.isReadyForCycle() == false) {
                System.out.println("---CPU FAILED TO CYCLE ERROR---");
                break;
            }
        }
    }

    public String encodeFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

        return new String (encoded, ENCODE_CHARSET);
    }
}