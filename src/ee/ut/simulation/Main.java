package ee.ut.simulation;

public class Main {

    public static void main(String[] args) {

        Device buyer = new Device(4000, 1000, 50, 125);

        Device seller = new Device(8000, 4000, 70, 30);

        Task task = new Task(3000000, 4);

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


    }

}
