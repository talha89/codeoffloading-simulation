package ee.ut.simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static double offloadingNotPlausibleCount = 0;
    private static double offloadingSuccesfullCount = 0;

    public static void main(String[] args) throws IOException {

        ArrayList<Device> buyerDevices;
        ArrayList<Device> sellerDevices;

        buyerDevices = generateRandomDevices(1000);
        sellerDevices = generateRandomDevices(1000);

        Task task = new Task(3000000, 0.5);

        for (int i = 0; i < 5; ++i) {

            if (i % 2 == 0)
                performOffloading(buyerDevices, (ArrayList<Device>) sellerDevices.clone(), task);
            else
                performOffloading(sellerDevices, (ArrayList<Device>) buyerDevices.clone(), task);

        }

        //performOffloading(buyerDevices, (ArrayList<Device>) sellerDevices.clone(), task);

        System.out.println("offloadingSuccesfullCount: " + offloadingSuccesfullCount);
        System.out.println("offloadingNotPlausibleCount: " + offloadingNotPlausibleCount);

        double failureRate = (offloadingNotPlausibleCount / (offloadingSuccesfullCount + offloadingNotPlausibleCount)) * 100;
        System.out.println("Failure rate:  " + failureRate);

    }

    static void performOffloading(ArrayList<Device> buyerDevices,
                                  ArrayList<Device> sellerDevices,
                                  Task task) throws IOException {

        boolean isOffloaded;
        double localTime;

        String csvFile = "C:\\Thesis-stuff\\simulation-results-multi-run.csv";
        FileWriter writer = new FileWriter(csvFile, true);

        CSVUtils.writeLine(writer, Arrays.asList("Local execution time", "Remote execution time", "Buyer Patience Factor",
                "Local device name", "Remote device name", "cost", "local money", "remote money",
                "Buyer reserve price", "Seller reserve price"));

        for (final Device buyer : buyerDevices) {

            isOffloaded = false;
            localTime = buyer.calculateExecutionTimeOfTask(task);

/*            if (localTime <= 4) {
                continue;
            }*/

            int buyerReservePrice = buyer.calculateReservePrice(OffloadingMode.BUYER);
            double buyerPatienceFactor = buyer.calculatePatienceFactor();
            double buyerPacketSendingCost = buyer.setPacketSendingCost(task);

/*            if(buyerPatienceFactor > 0.8){
                continue;
            }*/

            List<Device> filteredSellerDevices = sellerDevices.stream()
                    .filter(seller -> seller.getEffectiveMips() > buyer.getEffectiveMips())
                    .collect(Collectors.toList());

            for (Device seller : filteredSellerDevices) {

                double remoteTime = seller.calculateExecutionTimeOfTask(task) +
                        2 * seller.calculateTransmissionTime(task, buyer.getNetworkBandWidthAvailable());

/*                boolean isOffloadingPlausible = localTime > seller.calculateExecutionTimeOfTask(task) +
                        2 * seller.calculateTransmissionTime(task, buyer.getNetworkBandWidthAvailable());

                if (!isOffloadingPlausible) {

                    //System.out.println("Shouldn't offload - no time benefit");

                } else {*/

                int sellerReservePrice = seller.calculateReservePrice(OffloadingMode.SELLER);

                    double priceDifference = seller.calculateDifferenceValue(buyerReservePrice);
                if (priceDifference <= 0) {
                    //  System.out.println("Shouldn't offload - price difference is negative");
                        continue;
                    }

                double cost = seller.calculateInitialSellerOfferPrice(buyerReservePrice,
                        buyerPatienceFactor, buyerPacketSendingCost, task);

                if (cost > buyerReservePrice) {
                    cost = buyerReservePrice;
                } else if (cost < sellerReservePrice) {
                    cost = sellerReservePrice;
                }

                    isOffloaded = true;

                // System.out.println("local execution time " + localTime);

                // System.out.println("remote execution time " + seller.calculateExecutionTimeOfTask(task));

                // System.out.println("transmission time " + 2 * seller.calculateTransmissionTime(task, buyer.getNetworkBandWidthAvailable()));

                offloadingSuccesfullCount++;

                    CSVUtils.writeLine(writer, Arrays.asList(Double.toString(localTime),
                            Double.toString(remoteTime), Double.toString(buyerPatienceFactor),
                            buyer.getDeviceName(), seller.getDeviceName(), Double.toString(cost),
                            Double.toString(buyer.getCurrencyUnitsAvailable()), Double.toString(seller.getCurrencyUnitsAvailable()),
                            Double.toString(buyerReservePrice), Double.toString(sellerReservePrice)));

                // deduct the prices here - in actual system, code will be offloaded at this point and price deducted after
                // offloading is completed.

                buyer.deductCurrency((int) cost);
                seller.addCurrency((int) cost);

                sellerDevices.remove(seller);

                    break;
                // }

            }

            if (!isOffloaded) {
                offloadingNotPlausibleCount++;
                /*CSVUtils.writeLine(writer, Arrays.asList(Double.toString(localTime),
                        "NA", Double.toString(buyerPatienceFactor), buyer.getDeviceName()));*/
            }

        }

        writer.flush();
        writer.close();

    }

    private static ArrayList<Device> generateRandomDevices(int numberOfDevices) {

        ArrayList<Device> devices = new ArrayList<Device>();

        for (int i = 1; i < numberOfDevices; ++i) {
            devices.add(new Device());
        }

        return devices;

    }

}
