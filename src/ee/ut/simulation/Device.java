package ee.ut.simulation;

import java.util.Random;

public class Device {

    private int totalMemory; // in MB
    private int remainingMemory; // in MB
    private int remainingBattery; // in percentage
    private int currencyUnitsAvailable;

    private double networkBandWidthAvailable; // will be randomly generated upon device construction - in MBPS
    private int mips; // will be randomly generated upon device construction

    public Device(int totalMemory,
                  int remainingMemory,
                  int remainingBattery,
                  int currencyUnitsAvailable) {

        this.totalMemory = totalMemory;
        this.remainingMemory = remainingMemory;
        this.remainingBattery = remainingBattery;
        this.currencyUnitsAvailable = currencyUnitsAvailable;

        Random rand = new Random();

        networkBandWidthAvailable = 0.5 + Math.random() * (2 - 0.5);
        mips = rand.nextInt(2000000) + 5000;

    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
    }

    public int getRemainingMemory() {
        return remainingMemory;
    }

    public void setRemainingMemory(int remainingMemory) {
        this.remainingMemory = remainingMemory;
    }

    public int getRemainingBattery() {
        return remainingBattery;
    }

    public void setRemainingBattery(int remainingBattery) {
        this.remainingBattery = remainingBattery;
    }

    public int getCurrencyUnitsAvailable() {
        return currencyUnitsAvailable;
    }

    public void setCurrencyUnitsAvailable(int currencyUnitsAvailable) {
        this.currencyUnitsAvailable = currencyUnitsAvailable;
    }

    public double getNetworkBandWidthAvailable() {
        return networkBandWidthAvailable;
    }

    public void setNetworkBandWidthAvailable(double networkBandWidthAvailable) {
        this.networkBandWidthAvailable = networkBandWidthAvailable;
    }

    public int getRemainingMemoryPercentage() {
        return (remainingMemory / totalMemory) * 100;
    }

    public int getMips() {
        return mips;
    }

    public void setMips(int mips) {
        this.mips = mips;
    }

}
