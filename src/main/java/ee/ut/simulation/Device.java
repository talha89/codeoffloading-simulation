package ee.ut.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Device {

    private double alpha = 0.33;
    private double beta = 0.33;
    private double gamma = 0.33;

    private double sellerPacketReceivingCost;
    private double buyerPacketSendingCost;

    private String deviceName;

    private int totalMemory; // in MB
    private double remainingMemory; // in MB
    private double remainingBattery; // in ratio
    private double processorSpeed; // in the ange of 1.5 to 2.5 GHz
    private double processorFree; // in GHz
    private int currencyUnitsAvailable;

    private double networkBandWidthAvailable; // will be randomly generated upon device construction - in Mbps
    private int mips; // will be randomly generated upon device construction

    private ArrayList<Integer> paymentHistory;
    private ArrayList<Integer> earningHistory;

    public Device() {

        paymentHistory = new ArrayList<Integer>();
        earningHistory = new ArrayList<Integer>();

        deviceName = UUID.randomUUID().toString();

        Random rand = new Random();

        ArrayList<Integer> listOfTotalMemory = new ArrayList();
        listOfTotalMemory.add(1000);
        listOfTotalMemory.add(2000);
        listOfTotalMemory.add(4000);
        listOfTotalMemory.add(8000);

        totalMemory = listOfTotalMemory.get(0);
        remainingMemory = ((double) rand.nextInt(100) + 1) / 100 * totalMemory;
        remainingBattery = ((double) rand.nextInt(100) + 1) / 100;
        processorSpeed = 1.5 + ((double) rand.nextInt(100) + 1) / 100;
        processorFree = ((double) rand.nextInt(100) + 1) / 100 * processorSpeed;
        currencyUnitsAvailable = rand.nextInt(1000);

        networkBandWidthAvailable = 0.5 + Math.random() * (4 - 0.5);
        mips = rand.nextInt(1995000) + 5000;

    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
    }

    public double getRemainingMemory() {
        return remainingMemory;
    }

    public void setRemainingMemory(int remainingMemory) {
        this.remainingMemory = remainingMemory;
    }

    public double getRemainingBatteryRatio() {
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

    public double getRemainingMemoryRatio() {
        return remainingMemory / totalMemory;
    }

    public double getRemainingProessingPowerRatio() {
        return processorFree / processorSpeed;
    }

    public int getMips() {
        return mips;
    }

    public int getEffectiveMips() {
        return (int) (mips * getRemainingProessingPowerRatio());
    }

    public void setMips(int mips) {
        this.mips = mips;
    }

    public String getDeviceName() {
        return deviceName;
    }

    int log2(double x) {
        return log(x, 2);
    }

    int log(double x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

// Functions for price calculation

    private double getStatusMetric() {
        return alpha * log2(getRemainingMemoryRatio() * 100)
                + beta * log2(getRemainingBatteryRatio() * 100)
                + gamma * log2(getRemainingProessingPowerRatio() * 100);

        //return (alpha * getRemainingMemoryRatio() + beta * getRemainingBatteryRatio() + gamma * getRemainingProessingPowerRatio()) * 100;
    }

    public int calculateReservePrice(OffloadingMode offloadingMode) {

        if (offloadingMode == OffloadingMode.BUYER) {
            if (paymentHistory.size() > 0) {
                return (int) calculateAverage(paymentHistory);
            }
        } else {
            if (earningHistory.size() > 0) {
                return (int) calculateAverage(earningHistory);
            }
        }

        return (int) (currencyUnitsAvailable / getStatusMetric());
    }

    public double calculatePatienceFactor() {
        return getStatusMetric() / (6.64 * (alpha + beta + gamma)); //getStatusMetric() / (100 * (alpha + beta + gamma));
    }

    public double calculateDifferenceValue(double buyerReservePrice) {
        return buyerReservePrice - calculateReservePrice(OffloadingMode.SELLER);
    }

    public int calculateInitialSellerOfferPrice(double buyerReservePrice,
                                                double buyerPatienceFactor,
                                                double buyerPacketSendingCost,
                                                Task task) {

        double sellerReservePrice = calculateReservePrice(OffloadingMode.SELLER);
        double differenceValue = calculateDifferenceValue(buyerReservePrice);
        double sellerPatienceFactor = calculatePatienceFactor();
        setPacketReceivingCost(task);

        double sellerShare = (differenceValue - (buyerPatienceFactor * differenceValue) -
                (buyerPatienceFactor * sellerPatienceFactor * sellerPacketReceivingCost) +
                (buyerPatienceFactor * sellerPacketReceivingCost) - (buyerPatienceFactor * buyerPacketSendingCost) +
                buyerPacketSendingCost) / (differenceValue - differenceValue * buyerPatienceFactor * sellerPatienceFactor);

        System.out.println("sellerShare:  " + sellerShare);

        return (int) (sellerReservePrice + (sellerShare * differenceValue));

    }

    // functions for time calculations

    public double calculateExecutionTimeOfTask(Task task) {
        return task.getInstructionCount() / getEffectiveMips();
    }

    public double calculateTransmissionTime(Task task, double buyerBandWidth) {

        if (buyerBandWidth < getNetworkBandWidthAvailable()) {
            return (task.getDataSize() * 8) / buyerBandWidth;
        } else {
            return (task.getDataSize() * 8) / getNetworkBandWidthAvailable();
        }

    }

    //calculated on seller's end
    public double setPacketReceivingCost(Task task) {
        sellerPacketReceivingCost = ((task.getDataSize() * 8) / networkBandWidthAvailable) * 0.152;
        return sellerPacketReceivingCost;
    }

    // calculated on buyer's end
    public double setPacketSendingCost(Task task) {
        buyerPacketSendingCost = ((task.getDataSize() * 8) / networkBandWidthAvailable) * 0.152;
        return buyerPacketSendingCost;
    }

    public int deductCurrency(int amount) {
        paymentHistory.add(amount);
        currencyUnitsAvailable -= amount;
        return currencyUnitsAvailable;
    }

    public int addCurrency(int amount) {
        earningHistory.add(amount);
        currencyUnitsAvailable += amount;
        return currencyUnitsAvailable;
    }

    private double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

}
