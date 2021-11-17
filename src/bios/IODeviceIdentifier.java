package bios;

public class IODeviceIdentifier {
    public static final int NULL = 0, KEYBOARD = 1, NETWORK = 2, USB_STORAGE = 3;
    public static final String[] deviceNames = new String[]{"NULL", "KEYBOARD", "NETWORK", "USB_STORAGE"};
    public static final int numDevices = 4;

    public int getNumDevices() { return numDevices; }
    public String getDeviceName(int index) { return deviceNames[index]; }
}
