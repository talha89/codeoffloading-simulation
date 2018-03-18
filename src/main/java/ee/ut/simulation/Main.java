package ee.ut.simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws IOException {

        ArrayList<Device> buyerDevices;
        ArrayList<Device> sellerDevices;

        buyerDevices = generateRandomDevices(1000);
        sellerDevices = generateRandomDevices(500);

        Task task = new Task(3000000, 0.55);

/*        for (int i = 0 ; i< 30; ++i){
            performOffloading(buyerDevices, sellerDevices, task);
        }*/

        performOffloading(buyerDevices, sellerDevices, task);
    }

    static void performOffloading(ArrayList<Device> buyerDevices,
                                  ArrayList<Device> sellerDevices,
                                  Task task) throws IOException {

        boolean isOffloaded;
        double localTime;

        String csvFile = "C:\\Thesis-stuff\\simulation-results-test.csv";
        FileWriter writer = new FileWriter(csvFile, true);

        //CSVUtils.writeLine(writer, Arrays.asList("Local execution time", "Remote execution time"));

        for (Device buyer : buyerDevices) {

            isOffloaded = false;
            localTime = buyer.calculateExecutionTimeOfTask(task);

            if (localTime <= 4) {
                continue;
            }

            for (Device seller : sellerDevices) {

                boolean isOffloadingPlausible = localTime > seller.calculateExecutionTimeOfTask(task) +
                        2 * seller.calculateTransmissionTime(task, buyer.getNetworkBandWidthAvailable());

                double remoteTime = seller.calculateExecutionTimeOfTask(task) +
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

                    isOffloaded = true;

                    System.out.println("local execution time " + localTime);

                    System.out.println("remote execution time " + seller.calculateExecutionTimeOfTask(task));

                    System.out.println("transmission time " + 2 * seller.calculateTransmissionTime(task, buyer.getNetworkBandWidthAvailable()));

                    CSVUtils.writeLine(writer, Arrays.asList(Double.toString(localTime),
                            Double.toString(remoteTime), buyer.getDeviceName(), seller.getDeviceName()));

                    break;
                }

            }

            if (!isOffloaded) {
                CSVUtils.writeLine(writer, Arrays.asList(Double.toString(localTime),
                        "NA", buyer.getDeviceName()));
            }

        }

        writer.flush();
        writer.close();

    }

    static ArrayList<Device> generateRandomDevices(int numberOfDevices) {

        ArrayList<Device> devices = new ArrayList<Device>();

        Random rand = new Random();

        ArrayList<Integer> listOfTotalMemory = new ArrayList();
        listOfTotalMemory.add(1000);
        listOfTotalMemory.add(2000);
        listOfTotalMemory.add(4000);
        listOfTotalMemory.add(8000);

        for (int i = 1; i < numberOfDevices; ++i) {

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
