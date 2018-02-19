package ee.ut.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        ArrayList<Device> buyerDevices;
        ArrayList<Device> sellerDevices;

        buyerDevices = generateRandomDevices();
        sellerDevices = generateRandomDevices();

        Task task = new Task(3000000, 4);

        performOffloading(buyerDevices, sellerDevices, task);

 /*
        Device buyer = new Device(4000, 1000, 50, 125);

        Device seller = new Device(8000, 4000, 70, 30);


        // first the buyer has to look through the list of available sellers and see to which to offload depending on the speed gains

        boolean isOffloadingPlausible = buyer.calculateExecutionTimeOfTask(task) > seller.calculateExecutionTimeOfTask(task) +
                2 * seller.calculateTransmissionTime(task, buyer.getNetworkBandWidthAvailable());

        if (!isOffloadingPlausible) {
            System.out.println("Shouldn't offload");
        } else {
            System.out.println("Should offload");
        }

        double buyerReservePrice = buyer.calculateReservePrice();
        double buyerPatienceFactor = buyer.calculatePatienceFactor();

        System.out.println("buyer reserved price: " + buyerReservePrice);
        System.out.println("buyer patience factor: " + buyerPatienceFactor);

        System.out.println("seller reserve price: " + seller.calculateReservePrice());
        System.out.println("seller patience price: " + seller.calculatePatienceFactor());

        System.out.println("Difference in price: " + seller.calculateDifferenceValue(buyerReservePrice));

        System.out.println("Initial seller price: " + seller.calculateInitialSellerOfferPrice(buyerReservePrice, buyerPatienceFactor));

        // here we do the timing calculation and afterwards do the amount deduction/addition

        System.out.println("local execution time " + buyer.calculateExecutionTimeOfTask(task));

        System.out.println("remote execution time " + seller.calculateExecutionTimeOfTask(task));

        System.out.println("transmission time " + 2 * seller.calculateTransmissionTime(task, buyer.getNetworkBandWidthAvailable()));

        System.out.println("buyer network bandwidth " + buyer.getNetworkBandWidthAvailable());
        System.out.println("seller network bandwidth " + seller.getNetworkBandWidthAvailable());

        System.out.println("seller mips " + seller.getMips());
        System.out.println("buyer mips " + buyer.getMips());

*/

    }

    static void performOffloading(ArrayList<Device> buyerDevices,
                                  ArrayList<Device> sellerDevices,
                                  Task task) {

        for (Device buyer : buyerDevices) {

            for (Device seller : sellerDevices) {

                boolean isOffloadingPlausible = buyer.calculateExecutionTimeOfTask(task) > seller.calculateExecutionTimeOfTask(task) +
                        2 * seller.calculateTransmissionTime(task, buyer.getNetworkBandWidthAvailable());

                if (!isOffloadingPlausible) {

                    System.out.println("Shouldn't offload - no time benefit");

                } else {

                    double buyerReservePrice = buyer.calculateReservePrice();
                    double buyerPatienceFactor = buyer.calculatePatienceFactor();

                    double priceDifference = seller.calculateDifferenceValue(buyerReservePrice);
                    if (priceDifference < 0) {
                        System.out.println("Shouldn't offload - price difference is negative");
                        continue;
                    }

                    System.out.println("local execution time " + buyer.calculateExecutionTimeOfTask(task));

                    System.out.println("remote execution time " + seller.calculateExecutionTimeOfTask(task));

                    System.out.println("transmission time " + 2 * seller.calculateTransmissionTime(task, buyer.getNetworkBandWidthAvailable()));

                }

            }

        }

    }

    static ArrayList<Device> generateRandomDevices() {

        ArrayList<Device> devices = new ArrayList<>();

        Random rand = new Random();

        ArrayList<Integer> listOfTotalMemory = new ArrayList();
        listOfTotalMemory.add(1000);
        listOfTotalMemory.add(2000);
        listOfTotalMemory.add(4000);
        listOfTotalMemory.add(8000);

        for (int i = 1; i < 100; ++i) {

            Collections.shuffle(listOfTotalMemory);

            int totalMemory = listOfTotalMemory.get(0);
            int remainingMemory = rand.nextInt(8000);
            int remainingBattery = rand.nextInt(100);
            int currencyUnitsAvailable = rand.nextInt(1000);

            devices.add(new Device(totalMemory, remainingMemory, remainingBattery, currencyUnitsAvailable));


        }

        return devices;

    }




}
