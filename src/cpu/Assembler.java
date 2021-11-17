package cpu;

import java.util.ArrayList;
import java.util.Random;

public class Assembler {
    private static final int DEFAULT_MEMORY_ALLOCATION = 64;
    private static final int DEFAULT_PRIORITY = 0;

    private static Random random = new Random();

    public static int memory(String processText) {
        String readLine = processText.split("\n")[0];

        if (readLine.startsWith("MEMORY")) {
            String readValue = readLine.substring(1 + readLine.indexOf(' ')).trim();

            return Integer.valueOf(readValue);
        }

        return DEFAULT_MEMORY_ALLOCATION;
    }

    public static int priority(String processText) {
        String readLine = processText.split("\n")[1];

        if (readLine.startsWith("PRIORITY")) {
            String readValue = readLine.substring(1 + readLine.indexOf(' ')).trim();

            return Integer.valueOf(readValue);
        }

        return DEFAULT_PRIORITY;
    }

    public static ArrayList<Assembly> assemble(String processText) {
        ArrayList<Assembly> newProcess = new ArrayList<>();

        for (String readLine : processText.split("\n")) {
            if (readLine.startsWith("IO")) {
                String readValue = readLine.substring(1 + readLine.indexOf(' ')).trim();
                int numValue = Integer.valueOf(readValue);

                newProcess.add(new Assembly(Assembly.SEM_IN, 0)); //TODO IODEVICE
                newProcess.add(new Assembly(Assembly.IO, 0, numValue));
                newProcess.add(new Assembly(Assembly.SEM_OUT, 0));
            } else if (readLine.startsWith("CALCULATE")) {
                String readValue = readLine.substring(1 + readLine.indexOf(' ')).trim();
                int numValue = Integer.valueOf(readValue);

                newProcess.add(new Assembly(Assembly.CALCULATE, numValue/2 + random.nextInt(numValue)));
            } else if (readLine.startsWith("FORK")) {
                newProcess.add(new Assembly(Assembly.FORK, 0));
            }
        }

        newProcess.add(new Assembly(Assembly.END, 1));

        return newProcess;
    }
}
