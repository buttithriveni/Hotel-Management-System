
public class Booking implements Billable {
    private int bookingId;
    private Customer customer;
    private Room room;
    private int numberOfDays;
    private double totalAmount;
    private boolean checkedIn;

    public Booking(int bookingId, Customer customer,
                   Room room, int numberOfDays) {

        this.bookingId = bookingId;
        this.customer = customer;
        this.room = room;
        this.numberOfDays = numberOfDays;
        this.totalAmount = room.getPrice() * numberOfDays;
        this.checkedIn = false;

        room.setAvailable(false);
    }

    public int getBookingId() {
        return bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Room getRoom() {
        return room;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void checkIn() {
        checkedIn = true;
        System.out.println("Customer checked in successfully!");
    }

    public void checkOut() {
        checkedIn = false;
        room.setAvailable(true);
        System.out.println("Customer checked out successfully!");
    }

    public void displayBooking() {
        System.out.println("\n===== BOOKING DETAILS =====");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Customer: " + customer.getName());
        System.out.println("Room Number: " + room.getRoomNumber());
        System.out.println("Number of Days: " + numberOfDays);
        System.out.println("Total Amount: Rs. " + totalAmount);
        System.out.println("Checked In: " + checkedIn);
    }
    @Override
public double calculateBill() {
    return totalAmount;
}
}

