package bios.schedulers;

public enum ProcessState {
    NEW, READY, WAITING, EXECUTING, IO_REQUEST, IO_WAIT, TERMINATED
}
