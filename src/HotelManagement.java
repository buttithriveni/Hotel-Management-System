
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class HotelManagement {

    private ArrayList<Room> rooms;
    private ArrayList<Customer> customers;
    private ArrayList<Booking> bookings;

    public HotelManagement() {
        rooms = new ArrayList<>();
        customers = new ArrayList<>();
        bookings = new ArrayList<>();

        // Adding sample hotel rooms
        rooms.add(new Room(101, "Single", 1000));
        rooms.add(new Room(102, "Double", 1500));
        rooms.add(new Room(103, "Deluxe", 2500));
        rooms.add(new Room(104, "Suite", 4000));
        rooms.add(new Room(105, "Double", 1500));
    }

    // Add a new room
    public void addRoom(Room room) {
        if (findRoom(room.getRoomNumber()) != null) {
            System.out.println("Room number already exists!");
            return;
        }

        rooms.add(room);
        System.out.println("Room added successfully!");
    }

    // Find room using room number
    public Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    // Register customer
   
public void registerCustomer(Customer customer) {

    if (customer.getName() == null || customer.getName().trim().isEmpty()) {
        System.out.println("Customer name cannot be empty!");
        return;
    }

    if (customer.getPhone() == null ||
        !customer.getPhone().matches("[0-9]{10}")) {
        System.out.println("Invalid phone number! Enter 10 digits.");
        return;
    }

    if (customer.getEmail() == null ||
        !customer.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
        System.out.println("Invalid email address!");
        return;
    }

    if (findCustomer(customer.getCustomerId()) != null) {
        System.out.println("Customer ID already exists!");
        return;
    }

    customers.add(customer);
    System.out.println("Customer registered successfully!");
}

    // Find customer
    public Customer findCustomer(int customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    // Book a room
    public void bookRoom(int bookingId, int customerId,
                         int roomNumber, int days) {

        if (days <= 0) {
            System.out.println("Number of days must be positive!");
            return;
        }

        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                System.out.println("Booking ID already exists!");
                return;
            }
        }

        Customer customer = findCustomer(customerId);
        Room room = findRoom(roomNumber);

        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }

        if (room == null) {
            System.out.println("Room not found!");
            return;
        }

        if (!room.isAvailable()) {
            System.out.println("Room is already booked!");
            return;
        }

        Booking booking = new Booking(
            bookingId, customer, room, days
        );

       bookings.add(booking);
       Thread notificationThread = new Thread(
    new BookingNotification(customer.getName(), bookingId)
);

notificationThread.start();

try (FileWriter writer = new FileWriter("data/bookings.txt", true)) {
    writer.write("Booking ID: " + bookingId + "\n");
    writer.write("Customer: " + customer.getName() + "\n");
    writer.write("Room Number: " + roomNumber + "\n");
    writer.write("Number of Days: " + days + "\n");
    writer.write("Total Amount: Rs. " + booking.calculateBill() + "\n");
    writer.write("----------------------------\n");

    System.out.println("Booking saved to file successfully!");

} catch (IOException e) {
    System.out.println("Error saving booking: " + e.getMessage());
}

System.out.println("Room booked successfully!");
booking.displayBooking();
    }

    // Display available rooms
    public void showAvailableRooms() {
        System.out.println("\n===== AVAILABLE ROOMS =====");

        for (Room room : rooms) {
            if (room.isAvailable()) {
                room.displayRoom();
                System.out.println("--------------------");
            }
        }
    }

    // Display all customers
    public void showCustomers() {
        System.out.println("\n===== CUSTOMER LIST =====");

        for (Customer customer : customers) {
            customer.displayCustomer();
            System.out.println("--------------------");
        }
    }

    // Display all bookings
    public void showBookings() {
        System.out.println("\n===== BOOKING RECORDS =====");

        for (Booking booking : bookings) {
            booking.displayBooking();
        }
    }

    // Check in customer
    public void checkIn(int bookingId) {
        Booking booking = findBooking(bookingId);

        if (booking == null) {
            System.out.println("Booking not found!");
            return;
        }

        if (booking.isCheckedIn()) {
            System.out.println("Customer is already checked in!");
            return;
        }

        booking.checkIn();
    }

    // Check out customer
    public void checkOut(int bookingId) {
        Booking booking = findBooking(bookingId);

        if (booking == null) {
            System.out.println("Booking not found!");
            return;
        }

        if (!booking.isCheckedIn()) {
            System.out.println("Customer has not checked in!");
            return;
        }

        booking.checkOut();

       System.out.println("Final Bill: Rs. " + booking.calculateBill());
    }

    // Find booking
    public Booking findBooking(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }
}

