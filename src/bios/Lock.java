package bios;

public class Lock {
    private boolean isInUse = false;

    public Lock() {
    }

    public boolean ioIn() {
        if (this.isInUse) {
            return false;
        } else {
            this.isInUse = true;
            return true;
        }
    }

    public void ioOut() { this.isInUse = false; }

    public boolean isInUse() { return this.isInUse; }
}
