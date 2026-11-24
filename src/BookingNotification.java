

public class BookingNotification implements Runnable {

    private String customerName;
    private int bookingId;

    public BookingNotification(String customerName, int bookingId) {
        this.customerName = customerName;
        this.bookingId = bookingId;
    }

    @Override
    public void run() {
        System.out.println("\n[Background Thread] Booking confirmation sent!");
        System.out.println("Customer: " + customerName);
        System.out.println("Booking ID: " + bookingId);
    }
}
