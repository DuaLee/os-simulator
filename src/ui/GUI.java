package ui;

import bios.Process;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class GUI {
    private JTable taskListTable;
    private JSlider clockSpeedSlider;
    private JButton startButton;
    private JButton benchmarkSchedulerButton;
    private JSpinner programInstallationSpinner;
    private JSpinner bitcoinMinerSpinner;
    private JSpinner internetBrowserTabsSpinner;
    private JSpinner wordProcessorSpinner;
    private JSpinner externalStorageSpinner;
    private JSpinner networkSpinner;
    private JProgressBar ramUsage;
    private JProgressBar vramUsage;

    private JPanel mainPanel = new JPanel();
    private JButton addButton;
    private JButton addButton1;
    private JButton addButton2;
    private JButton addButton3;
    private JButton addButton4;
    private JButton addButton5;
    private JButton resetButton;
    private JButton clearButton;
    private JTextArea textArea1;
    private JCheckBox autoScrollCheckBox;
    private JTextField memoryField;
    private JSlider threadsSlider;
    private JButton killButton;

    private OutputStream outputStream;
    private StringBuilder stringBuilder;

    private DefaultTableModel tableData = new DefaultTableModel();

    private Setup setup;

    //benchmark multiplier
    private final int BENCHMARK_MULTIPLIER = 2;

    //software filenames list to be referenced
    public final String[] SOFTWARE_LIST =
            {"Program_Installation.os", "Bitcoin_Miner.os", "Internet_Browser_Tabs.os",
                    "Word_Processor.os", "External_Storage.os", "Network.os"};

    //number of sequential processes of a software to be added to the queue
    private int[] processCount = new int[SOFTWARE_LIST.length];

    //final software selected list to be queued
    private ArrayList<String> processQueue;

    public GUI() {
        start();
        System.setOut(new PrintStream(outputStream, true));

        //UI theme - comment out line to return to java default

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Setup

        processQueue = new ArrayList<>();

        $$$setupUI$$$();

        //Start button listener

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!processQueue.isEmpty()) {
                    Thread worker = new Thread() {
                        public void run() {

//                    for (int i = 0; i < processQueue.size(); i++)
//                        System.out.println(processQueue.get(i));
                            setup = new Setup(processQueue, clockSpeedSlider.getValue(), Integer.parseInt(memoryField.getText()), threadsSlider.getValue(), outputStream);

                            try {
                                setup.start();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    };

                    worker.start();

                    ((DefaultTableModel) taskListTable.getModel()).setRowCount(0);

                    toggleButtons();
                } else
                    System.out.println(("Process queue empty"));
            }
        });

        //Benchmark button listener

        benchmarkSchedulerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < BENCHMARK_MULTIPLIER; i++)
                    for (int j = 0; j < SOFTWARE_LIST.length; j++)
                        processQueue.add(SOFTWARE_LIST[j]);

                System.out.println("Benchmark initialized with " + BENCHMARK_MULTIPLIER + " * " + SOFTWARE_LIST.length + " programs");
            }
        });

        //Clear button listener

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                programInstallationSpinner.setValue(0);
                bitcoinMinerSpinner.setValue(0);
                internetBrowserTabsSpinner.setValue(0);
                wordProcessorSpinner.setValue(0);
                externalStorageSpinner.setValue(0);
                networkSpinner.setValue(0);

                System.out.println("Cleared fields");
            }
        });

        //Reset button listener

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processQueue.clear();

                System.out.println("Emptied process queue");
            }
        });

        //Add button listener

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCount[0] = (int) programInstallationSpinner.getValue();

                for (int i = 0; i < processCount[0]; i++)
                    processQueue.add(SOFTWARE_LIST[0]);

                System.out.println("Added " + programInstallationSpinner.getValue() + " " + SOFTWARE_LIST[0]);
            }
        });

        addButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCount[1] = (int) bitcoinMinerSpinner.getValue();

                for (int i = 0; i < processCount[1]; i++)
                    processQueue.add(SOFTWARE_LIST[1]);

                System.out.println("Added " + bitcoinMinerSpinner.getValue() + " " + SOFTWARE_LIST[1]);
            }
        });

        addButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCount[2] = (int) internetBrowserTabsSpinner.getValue();

                for (int i = 0; i < processCount[2]; i++)
                    processQueue.add(SOFTWARE_LIST[2]);

                System.out.println("Added " + internetBrowserTabsSpinner.getValue() + " " + SOFTWARE_LIST[2]);
            }
        });

        addButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCount[3] = (int) wordProcessorSpinner.getValue();

                for (int i = 0; i < processCount[3]; i++)
                    processQueue.add(SOFTWARE_LIST[3]);

                System.out.println("Added " + wordProcessorSpinner.getValue() + " " + SOFTWARE_LIST[3]);
            }
        });

        addButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCount[4] = (int) externalStorageSpinner.getValue();

                for (int i = 0; i < processCount[4]; i++)
                    processQueue.add(SOFTWARE_LIST[4]);

                System.out.println("Added " + externalStorageSpinner.getValue() + " " + SOFTWARE_LIST[4]);
            }
        });

        addButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCount[5] = (int) networkSpinner.getValue();

                for (int i = 0; i < processCount[5]; i++)
                    processQueue.add(SOFTWARE_LIST[5]);

                System.out.println("Added " + networkSpinner.getValue() + " " + SOFTWARE_LIST[5]);
            }
        });
        killButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Attempting to forcefully terminate");

                setup.running = false;

                programInstallationSpinner.setValue(0);
                bitcoinMinerSpinner.setValue(0);
                internetBrowserTabsSpinner.setValue(0);
                wordProcessorSpinner.setValue(0);
                externalStorageSpinner.setValue(0);
                networkSpinner.setValue(0);
                processQueue.clear();

                System.out.println("All processes forcefully terminated");

                toggleButtons();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Operating System Simulation");
        frame.setContentPane(new GUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        stringBuilder = new StringBuilder();
        outputStream = new OutputStream() {
            public void write(int b) throws IOException {
                stringBuilder.append((char) b);

                if (((char) b) == '\n')
                    log();
            }
        };
    }

    public void log(String message) {
        textArea1.append(message);

        if (autoScrollCheckBox.isSelected())
            textArea1.setCaretPosition(textArea1.getDocument().getLength());
    }

    public void log() {
        String line = stringBuilder.toString();
        if (line.contains("RAM usage: ")) {
            //log(Integer.toString((Integer.parseInt(line.substring(line.indexOf("RAM usage: ") + 11, line.indexOf("kB"))) * 100 / setup.cpu.getMemorySize() * 100) / 100));
            ramUsage.setValue((Integer.parseInt(line.substring(line.indexOf("RAM usage: ") + 11, line.indexOf("kB"))) * 100 / setup.cpu.getMemorySize() * 100) / 100);

            int committed = 0;

            for (Process process : setup.cpu.kernel.processList)
                committed += process.getMemoryLimit();

            vramUsage.setValue((Integer.parseInt(line.substring(line.indexOf("VRAM usage: ") + 12, line.lastIndexOf("kB"))) * 100 / committed * 100) / 100);

            //table updates each time the cpu cycles
            ((DefaultTableModel) taskListTable.getModel()).setRowCount(0);

            for (Process process : setup.cpu.kernel.processList) {
                String priority = "Unspecified";

                if (process.getPriority() >= 10)
                    priority = "Very High";
                else if (process.getPriority() >= 5)
                    priority = "High";
                else if (process.getPriority() >= 0)
                    priority = "Normal";
                else if (process.getPriority() >= -5)
                    priority = "Low";
                else if (process.getPriority() < -5)
                    priority = "Very Low";

                tableData.addRow(new String[]{process.getProcessID() + "", process.getProcessName(), process.getState().name(), process.getCpuCyclesUsed() + "", process.getMemoryLimit() + "kB", "[" + process.getPriority() + "] " + priority, process.getWeightedPriority() + ""});
            }
        }

        if (stringBuilder.length() > 0) {
            log(line);
            stringBuilder.setLength(0);
        }

        if (line.contains("All processes terminated")) {
            ramUsage.setValue(0);
            vramUsage.setValue(0);
            toggleButtons();
        }
    }

    public void toggleButtons() {
        if (addButton.isEnabled()) {
            addButton.setEnabled(false);
            addButton1.setEnabled(false);
            addButton2.setEnabled(false);
            addButton3.setEnabled(false);
            addButton4.setEnabled(false);
            addButton5.setEnabled(false);
            clockSpeedSlider.setEnabled(false);
            memoryField.setEnabled(false);
            threadsSlider.setEnabled(false);
            benchmarkSchedulerButton.setEnabled(false);
            startButton.setEnabled(false);
            resetButton.setEnabled(false);
            clearButton.setEnabled(false);
        } else {
            addButton.setEnabled(true);
            addButton1.setEnabled(true);
            addButton2.setEnabled(true);
            addButton3.setEnabled(true);
            addButton4.setEnabled(true);
            addButton5.setEnabled(true);
            clockSpeedSlider.setEnabled(true);
            memoryField.setEnabled(true);
            threadsSlider.setEnabled(true);
            benchmarkSchedulerButton.setEnabled(true);
            startButton.setEnabled(true);
            resetButton.setEnabled(true);
            clearButton.setEnabled(true);
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(22, 5, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setBorder(BorderFactory.createTitledBorder(null, "Task Manager", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        startButton = new JButton();
        startButton.setEnabled(true);
        Font startButtonFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, startButton.getFont());
        if (startButtonFont != null) startButton.setFont(startButtonFont);
        startButton.setText("Start");
        mainPanel.add(startButton, new com.intellij.uiDesigner.core.GridConstraints(21, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(400, 30), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setEnabled(true);
        Font label1Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Program Installation (CPU NORMAL)");
        mainPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(14, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Bitcoin Miner (CPU HIGH)");
        mainPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(15, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Internet Browser Tabs (CPU LOW)");
        mainPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(16, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Word Processor (IO NORMAL)");
        mainPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(17, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("External Storage (IO HIGH)");
        mainPanel.add(label5, new com.intellij.uiDesigner.core.GridConstraints(18, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setEnabled(true);
        Font label6Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, label6.getFont());
        if (label6Font != null) label6.setFont(label6Font);
        label6.setText("Network (IO LOW)");
        mainPanel.add(label6, new com.intellij.uiDesigner.core.GridConstraints(19, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$("Trebuchet MS", Font.BOLD, -1, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("Task List");
        mainPanel.add(label7, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$("Trebuchet MS", Font.BOLD, -1, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        label8.setText("Software");
        mainPanel.add(label8, new com.intellij.uiDesigner.core.GridConstraints(13, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        Font label9Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, label9.getFont());
        if (label9Font != null) label9.setFont(label9Font);
        label9.setText("RAM Usage");
        mainPanel.add(label9, new com.intellij.uiDesigner.core.GridConstraints(20, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHEAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ramUsage = new JProgressBar();
        ramUsage.setStringPainted(true);
        mainPanel.add(ramUsage, new com.intellij.uiDesigner.core.GridConstraints(21, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        Font label10Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        label10.setText("VRAM Usage");
        mainPanel.add(label10, new com.intellij.uiDesigner.core.GridConstraints(20, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHEAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        vramUsage = new JProgressBar();
        vramUsage.setStringPainted(true);
        mainPanel.add(vramUsage, new com.intellij.uiDesigner.core.GridConstraints(21, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        benchmarkSchedulerButton = new JButton();
        Font benchmarkSchedulerButtonFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, benchmarkSchedulerButton.getFont());
        if (benchmarkSchedulerButtonFont != null) benchmarkSchedulerButton.setFont(benchmarkSchedulerButtonFont);
        benchmarkSchedulerButton.setText("Scheduler Benchmark");
        mainPanel.add(benchmarkSchedulerButton, new com.intellij.uiDesigner.core.GridConstraints(20, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(30);
        scrollPane1.setVerticalScrollBarPolicy(20);
        mainPanel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        taskListTable.setAutoCreateRowSorter(false);
        taskListTable.setDoubleBuffered(false);
        taskListTable.setFillsViewportHeight(false);
        taskListTable.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
        taskListTable.putClientProperty("Table.isFileList", Boolean.FALSE);
        scrollPane1.setViewportView(taskListTable);
        addButton1 = new JButton();
        Font addButton1Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, addButton1.getFont());
        if (addButton1Font != null) addButton1.setFont(addButton1Font);
        addButton1.setText("Add");
        mainPanel.add(addButton1, new com.intellij.uiDesigner.core.GridConstraints(15, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addButton2 = new JButton();
        Font addButton2Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, addButton2.getFont());
        if (addButton2Font != null) addButton2.setFont(addButton2Font);
        addButton2.setText("Add");
        mainPanel.add(addButton2, new com.intellij.uiDesigner.core.GridConstraints(16, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addButton3 = new JButton();
        Font addButton3Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, addButton3.getFont());
        if (addButton3Font != null) addButton3.setFont(addButton3Font);
        addButton3.setText("Add");
        mainPanel.add(addButton3, new com.intellij.uiDesigner.core.GridConstraints(17, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addButton4 = new JButton();
        Font addButton4Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, addButton4.getFont());
        if (addButton4Font != null) addButton4.setFont(addButton4Font);
        addButton4.setText("Add");
        mainPanel.add(addButton4, new com.intellij.uiDesigner.core.GridConstraints(18, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addButton5 = new JButton();
        Font addButton5Font = this.$$$getFont$$$("Trebuchet MS", -1, -1, addButton5.getFont());
        if (addButton5Font != null) addButton5.setFont(addButton5Font);
        addButton5.setText("Add");
        mainPanel.add(addButton5, new com.intellij.uiDesigner.core.GridConstraints(19, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        programInstallationSpinner = new JSpinner();
        programInstallationSpinner.setEnabled(true);
        programInstallationSpinner.setFocusCycleRoot(false);
        programInstallationSpinner.setFocusTraversalPolicyProvider(false);
        Font programInstallationSpinnerFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, programInstallationSpinner.getFont());
        if (programInstallationSpinnerFont != null) programInstallationSpinner.setFont(programInstallationSpinnerFont);
        programInstallationSpinner.setOpaque(true);
        mainPanel.add(programInstallationSpinner, new com.intellij.uiDesigner.core.GridConstraints(14, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bitcoinMinerSpinner = new JSpinner();
        Font bitcoinMinerSpinnerFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, bitcoinMinerSpinner.getFont());
        if (bitcoinMinerSpinnerFont != null) bitcoinMinerSpinner.setFont(bitcoinMinerSpinnerFont);
        mainPanel.add(bitcoinMinerSpinner, new com.intellij.uiDesigner.core.GridConstraints(15, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        internetBrowserTabsSpinner = new JSpinner();
        Font internetBrowserTabsSpinnerFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, internetBrowserTabsSpinner.getFont());
        if (internetBrowserTabsSpinnerFont != null) internetBrowserTabsSpinner.setFont(internetBrowserTabsSpinnerFont);
        mainPanel.add(internetBrowserTabsSpinner, new com.intellij.uiDesigner.core.GridConstraints(16, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordProcessorSpinner = new JSpinner();
        Font wordProcessorSpinnerFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, wordProcessorSpinner.getFont());
        if (wordProcessorSpinnerFont != null) wordProcessorSpinner.setFont(wordProcessorSpinnerFont);
        mainPanel.add(wordProcessorSpinner, new com.intellij.uiDesigner.core.GridConstraints(17, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        externalStorageSpinner = new JSpinner();
        Font externalStorageSpinnerFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, externalStorageSpinner.getFont());
        if (externalStorageSpinnerFont != null) externalStorageSpinner.setFont(externalStorageSpinnerFont);
        mainPanel.add(externalStorageSpinner, new com.intellij.uiDesigner.core.GridConstraints(18, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        networkSpinner = new JSpinner();
        Font networkSpinnerFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, networkSpinner.getFont());
        if (networkSpinnerFont != null) networkSpinner.setFont(networkSpinnerFont);
        mainPanel.add(networkSpinner, new com.intellij.uiDesigner.core.GridConstraints(19, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addButton = new JButton();
        Font addButtonFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, addButton.getFont());
        if (addButtonFont != null) addButton.setFont(addButtonFont);
        addButton.setText("Add");
        mainPanel.add(addButton, new com.intellij.uiDesigner.core.GridConstraints(14, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resetButton = new JButton();
        Font resetButtonFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, resetButton.getFont());
        if (resetButtonFont != null) resetButton.setFont(resetButtonFont);
        resetButton.setText("Reset");
        mainPanel.add(resetButton, new com.intellij.uiDesigner.core.GridConstraints(21, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearButton = new JButton();
        Font clearButtonFont = this.$$$getFont$$$("Trebuchet MS", -1, -1, clearButton.getFont());
        if (clearButtonFont != null) clearButton.setFont(clearButtonFont);
        clearButton.setText("Clear");
        mainPanel.add(clearButton, new com.intellij.uiDesigner.core.GridConstraints(20, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        Font label11Font = this.$$$getFont$$$("Trebuchet MS", Font.BOLD, -1, label11.getFont());
        if (label11Font != null) label11.setFont(label11Font);
        label11.setText("System Log");
        mainPanel.add(label11, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setHorizontalScrollBarPolicy(31);
        mainPanel.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 73), new Dimension(-1, 73), null, 0, false));
        textArea1 = new JTextArea();
        textArea1.setAutoscrolls(true);
        textArea1.setEditable(false);
        textArea1.setLineWrap(true);
        textArea1.setText("");
        scrollPane2.setViewportView(textArea1);
        autoScrollCheckBox = new JCheckBox();
        autoScrollCheckBox.setSelected(true);
        autoScrollCheckBox.setText("Auto-Scroll");
        mainPanel.add(autoScrollCheckBox, new com.intellij.uiDesigner.core.GridConstraints(5, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(14, 2, 6, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(137, 11), null, 0, false));
        final JLabel label12 = new JLabel();
        Font label12Font = this.$$$getFont$$$("Trebuchet MS", Font.BOLD, -1, label12.getFont());
        if (label12Font != null) label12.setFont(label12Font);
        label12.setText("System Parameters");
        mainPanel.add(label12, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        Font label13Font = this.$$$getFont$$$("Trebuchet MS", Font.BOLD, -1, label13.getFont());
        if (label13Font != null) label13.setFont(label13Font);
        label13.setText("Physical Memory (kB)");
        mainPanel.add(label13, new com.intellij.uiDesigner.core.GridConstraints(8, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        memoryField = new JTextField();
        memoryField.setText("2048");
        mainPanel.add(memoryField, new com.intellij.uiDesigner.core.GridConstraints(9, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator1 = new JSeparator();
        mainPanel.add(separator1, new com.intellij.uiDesigner.core.GridConstraints(12, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator2 = new JSeparator();
        mainPanel.add(separator2, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        mainPanel.add(separator3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        clockSpeedSlider = new JSlider();
        clockSpeedSlider.setInheritsPopupMenu(false);
        clockSpeedSlider.setInverted(false);
        clockSpeedSlider.setMajorTickSpacing(100);
        clockSpeedSlider.setMaximum(1000);
        clockSpeedSlider.setMinimum(0);
        clockSpeedSlider.setMinorTickSpacing(10);
        clockSpeedSlider.setName("");
        clockSpeedSlider.setOrientation(0);
        clockSpeedSlider.setPaintLabels(false);
        clockSpeedSlider.setPaintTicks(true);
        clockSpeedSlider.setPaintTrack(true);
        clockSpeedSlider.setSnapToTicks(true);
        clockSpeedSlider.setToolTipText("");
        clockSpeedSlider.setValue(100);
        mainPanel.add(clockSpeedSlider, new com.intellij.uiDesigner.core.GridConstraints(11, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        Font label14Font = this.$$$getFont$$$("Trebuchet MS", Font.BOLD, -1, label14.getFont());
        if (label14Font != null) label14.setFont(label14Font);
        label14.setText("Clock Speed (0 - 1000 ms)");
        mainPanel.add(label14, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        Font label15Font = this.$$$getFont$$$("Trebuchet MS", Font.BOLD, -1, label15.getFont());
        if (label15Font != null) label15.setFont(label15Font);
        label15.setText("Threads");
        mainPanel.add(label15, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        threadsSlider = new JSlider();
        threadsSlider.setInheritsPopupMenu(false);
        threadsSlider.setInverted(false);
        threadsSlider.setMajorTickSpacing(1);
        threadsSlider.setMaximum(8);
        threadsSlider.setMinimum(1);
        threadsSlider.setMinorTickSpacing(0);
        threadsSlider.setName("");
        threadsSlider.setOrientation(0);
        threadsSlider.setPaintLabels(true);
        threadsSlider.setPaintTicks(true);
        threadsSlider.setPaintTrack(true);
        threadsSlider.setSnapToTicks(true);
        threadsSlider.setToolTipText("");
        threadsSlider.setValue(1);
        mainPanel.add(threadsSlider, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        killButton = new JButton();
        killButton.setText("Kill");
        mainPanel.add(killButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private void createUIComponents() {
        taskListTable = new JTable(tableData);
        tableData.setColumnIdentifiers(new String[]{"PID", "Name", "Status", "CPU", "Memory", "Priority", "Weighted Priority"});
        //tableData.addRow(new String[]{"1234", "test program", "Running", "325", "500kB", "Normal", "0"});
    }
}