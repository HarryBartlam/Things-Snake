package com.simplyapp.max72xx.driver;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

/**
 * This is a class copied from https://github.com/Nilhcem/ledcontrol-androidthings
 * See original license at https://github.com/Nilhcem/ledcontrol-androidthings/blob/master/LICENSE
 * This project/git class was copied to allow more device then the previous 8, now 16. see maxDevicesLimit
 */
public class MAX72xxLEDController implements AutoCloseable {

    //the opcodes for the MAX7221 and MAX7219
    private static final byte OP_DIGIT0 = 1;
    private static final byte OP_DECODEMODE = 9;
    private static final byte OP_INTENSITY = 10;
    private static final byte OP_SCANLIMIT = 11;
    private static final byte OP_SHUTDOWN = 12;
    private static final byte OP_DISPLAYTEST = 15;

    private SpiDevice spiDevice;

    /* The array for shifting the data to the devices */
    private byte[] spidata = new byte[32];

    /* We keep track of the led-status for all 8 devices in this array */
    private byte[] status = new byte[128];

    /* The maximum number of devices we use */
    private int maxDevices;

    /* Added to allow more then the 8 device allowed in the original code */
    private int maxDevicesLimit = 16;

    public MAX72xxLEDController(String spiGpio, int numDevices) throws IOException {
        PeripheralManager manager = PeripheralManager.getInstance();
        spiDevice = manager.openSpiDevice(spiGpio);
        spiDevice.setMode(SpiDevice.MODE0);
        spiDevice.setFrequency(1000000); // 1MHz
        spiDevice.setBitsPerWord(8); // 8 BPW
        spiDevice.setBitJustification(false); // MSB first

        maxDevices = numDevices;
        if (numDevices < 1 || numDevices > maxDevicesLimit) {
            maxDevices = maxDevicesLimit;
        }

        for (int i = 0; i < maxDevices; i++) {
            spiTransfer(i, OP_DISPLAYTEST, 0);
            setScanLimit(i, 7); // scanlimit: 8 LEDs
            spiTransfer(i, OP_DECODEMODE, 0); // decoding： BCD
            clearDisplay(i);
            // we go into shutdown-mode on startup
            shutdown(i, true);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            spiDevice.close();
        } finally {
            spiDevice = null;
        }
    }

    /**
     * Get the number of devices attached to this LedControl.
     *
     * @return the number of devices on this LedControl
     */
    public int getDeviceCount() {
        return maxDevices;
    }

    /**
     * Set the shutdown (power saving) mode for the device
     *
     * @param addr   the address of the display to control
     * @param status if true the device goes into power-down mode. Set to false for normal operation.
     */
    public void shutdown(int addr, boolean status) throws IOException {
        if (addr < 0 || addr >= maxDevices) {
            return;
        }

        spiTransfer(addr, OP_SHUTDOWN, status ? 0 : 1);
    }

    /**
     * Set the number of digits (or rows) to be displayed.
     * <p>
     * See datasheet for sideeffects of the scanlimit on the brightness of the display
     * </p>
     *
     * @param addr  the address of the display to control
     * @param limit number of digits to be displayed (1..8)
     */
    public void setScanLimit(int addr, int limit) throws IOException {
        if (addr < 0 || addr >= maxDevices) {
            return;
        }

        if (limit >= 0 || limit < 8) {
            spiTransfer(addr, OP_SCANLIMIT, limit);
        }
    }

    /**
     * Set the brightness of the display
     *
     * @param addr      the address of the display to control
     * @param intensity the brightness of the display. (0..15)
     */
    public void setIntensity(int addr, int intensity) throws IOException {
        if (addr < 0 || addr >= maxDevices) {
            return;
        }

        if (intensity >= 0 || intensity < 16) {
            spiTransfer(addr, OP_INTENSITY, intensity);
        }
    }

    /**
     * Switch all Leds on the display off
     *
     * @param addr the address of the display to control
     */
    public void clearDisplay(int addr) throws IOException {
        if (addr < 0 || addr >= maxDevices) {
            return;
        }

        int offset = addr * 8;
        for (int i = 0; i < 8; i++) {
            status[offset + i] = 0;
            spiTransfer(addr, (byte) (OP_DIGIT0 + i), status[offset + i]);
        }
    }

    /**
     * Set the status of a single Led
     *
     * @param addr  the address of the display to control
     * @param row   the row of the Led (0..7)
     * @param col   the column of the Led (0..7)
     * @param state if true the led is switched on, if false it is switched off
     * @throws IOException
     */
    public void setLed(int addr, int row, int col, boolean state) throws IOException {
        if (addr < 0 || addr >= maxDevices) {
            return;
        }
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return;
        }

        int offset = addr * 8;
        byte val = (byte) (0b10000000 >> col);
        if (state) {
            status[offset + row] = (byte) (status[offset + row] | val);
        } else {
            val = (byte) ~val;
            status[offset + row] = (byte) (status[offset + row] & val);
        }
        spiTransfer(addr, (byte) (OP_DIGIT0 + row), status[offset + row]);
    }


    /**
     * Send out a single command to the device
     */
    private void spiTransfer(int addr, byte opcode, int data) throws IOException {
        int offset = addr * 2;
        int maxbytes = maxDevices * 2;

        for (int i = 0; i < maxbytes; i++) {
            spidata[i] = (byte) 0;
        }

        // put our device data into the array
        spidata[maxbytes - offset - 2] = opcode;
        spidata[maxbytes - offset - 1] = (byte) data;
        spiDevice.write(spidata, maxbytes);
    }
}