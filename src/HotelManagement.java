
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

    loadDataFromDatabase();
}

private void loadDataFromDatabase() {

    try (java.sql.Connection con = DBConnection.getConnection()) {

        // Load customers
        String customerSQL = "SELECT * FROM customers";

        try (java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery(customerSQL)) {

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email")
                ));
            }
        }

        // Load rooms
        String roomSQL = "SELECT * FROM rooms";

        try (java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery(roomSQL)) {

            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("room_number"),
                    rs.getString("room_type"),
                    rs.getDouble("price")
                );

                room.setAvailable(
                    "Available".equalsIgnoreCase(rs.getString("status"))
                );

                rooms.add(room);
            }
        }

        // Load existing bookings
        String bookingSQL = "SELECT * FROM bookings";

        try (java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery(bookingSQL)) {

            while (rs.next()) {

                Customer customer = findCustomer(rs.getInt("customer_id"));
                Room room = findRoom(rs.getInt("room_number"));

                if (customer != null && room != null) {

                    Booking booking = new Booking(
                        rs.getInt("booking_id"),
                        customer,
                        room,
                        rs.getInt("days")
                    );

                    String status = rs.getString("status");

                    if ("Checked In".equals(status)) {
                        booking.checkIn();
                    } else if ("Checked Out".equals(status)) {
                        booking.checkIn();
                        booking.checkOut();
                    }

                    bookings.add(booking);
                }
            }
        }


        System.out.println("Customers and rooms loaded from MySQL!");

    } catch (java.sql.SQLException e) {
        System.out.println("Database loading error: " + e.getMessage());
    }
}


    // Add a new room
   public void addRoom(Room room) {

    if (findRoom(room.getRoomNumber()) != null) {
        System.out.println("Room number already exists!");
        return;
    }

    String sql = "INSERT INTO rooms (room_number, room_type, price, status) VALUES (?, ?, ?, ?)";

    try (java.sql.Connection con = DBConnection.getConnection();
         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, room.getRoomNumber());
        ps.setString(2, room.getRoomType());
        ps.setDouble(3, room.getPrice());
        ps.setString(4, "Available");

        ps.executeUpdate();

        rooms.add(room);
        System.out.println("Room added and saved in MySQL!");

    } catch (java.sql.SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
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

    // 1. Validate customer name
    if (customer.getName() == null ||
        customer.getName().trim().isEmpty()) {
        System.out.println("Customer name cannot be empty!");
        return;
    }

    // 2. Validate phone
    if (customer.getPhone() == null ||
        !customer.getPhone().matches("[0-9]{10}")) {
        System.out.println("Invalid phone number! Enter 10 digits.");
        return;
    }

    // 3. Validate email
    if (customer.getEmail() == null ||
        !customer.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
        System.out.println("Invalid email address!");
        return;
    }

    // 4. Check duplicate customer ID
    if (findCustomer(customer.getCustomerId()) != null) {
        System.out.println("Customer ID already exists!");
        return;
    }

    // 5. Save customer in MySQL
    String sql = "INSERT INTO customers (customer_id, name, phone, email) VALUES (?, ?, ?, ?)";

    try (java.sql.Connection con = DBConnection.getConnection();
         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, customer.getCustomerId());
        ps.setString(2, customer.getName());
        ps.setString(3, customer.getPhone());
        ps.setString(4, customer.getEmail());

        ps.executeUpdate();

        // 6. Add to Java collection after database success
        customers.add(customer);

        System.out.println("Customer registered successfully!");
        System.out.println("Customer saved in MySQL database!");

    } catch (java.sql.SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
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

    for (Booking b : bookings) {
        if (b.getBookingId() == bookingId) {
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

    String insertSQL = "INSERT INTO bookings " +
        "(booking_id, customer_id, room_number, days, total_amount, status) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    String updateSQL =
        "UPDATE rooms SET status = 'Booked' WHERE room_number = ?";

    try (java.sql.Connection con = DBConnection.getConnection()) {

        con.setAutoCommit(false);

        try (java.sql.PreparedStatement ps =
                 con.prepareStatement(insertSQL);
             java.sql.PreparedStatement roomPS =
                 con.prepareStatement(updateSQL)) {

            ps.setInt(1, bookingId);
            ps.setInt(2, customerId);
            ps.setInt(3, roomNumber);
            ps.setInt(4, days);
            ps.setDouble(5, booking.calculateBill());
            ps.setString(6, "Booked");

            ps.executeUpdate();

            roomPS.setInt(1, roomNumber);
            roomPS.executeUpdate();

            con.commit();

            bookings.add(booking);
            room.setAvailable(false);

            System.out.println("Room booked successfully!");
            System.out.println("Booking saved in MySQL!");

            Thread notificationThread = new Thread(
                new BookingNotification(customer.getName(), bookingId)
            );
            notificationThread.start();

        } catch (java.sql.SQLException e) {
            con.rollback();
            System.out.println("Booking failed: " + e.getMessage());
        }

    } catch (java.sql.SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
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

    String bookingSQL =
        "UPDATE bookings SET status = 'Checked Out' WHERE booking_id = ?";

    String roomSQL =
        "UPDATE rooms SET status = 'Available' " +
        "WHERE room_number = (SELECT room_number FROM bookings WHERE booking_id = ?)";

    try (java.sql.Connection con = DBConnection.getConnection()) {

        con.setAutoCommit(false);

        try (java.sql.PreparedStatement ps1 = con.prepareStatement(bookingSQL);
             java.sql.PreparedStatement ps2 = con.prepareStatement(roomSQL)) {

            ps1.setInt(1, bookingId);
            ps1.executeUpdate();

            ps2.setInt(1, bookingId);
            ps2.executeUpdate();

            con.commit();

            booking.checkOut();

            System.out.println("Check-out saved in MySQL!");
            System.out.println("Final Bill: Rs. " + booking.calculateBill());

        } catch (java.sql.SQLException e) {
            con.rollback();
            System.out.println("Check-out failed: " + e.getMessage());
        }

    } catch (java.sql.SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
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

public void viewCustomers() {

    String sql = "SELECT * FROM customers";

    try (java.sql.Connection con = DBConnection.getConnection();
         java.sql.Statement st = con.createStatement();
         java.sql.ResultSet rs = st.executeQuery(sql)) {

        System.out.println("\n--- ALL CUSTOMERS ---");

        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("customer_id"));
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Phone: " + rs.getString("phone"));
            System.out.println("Email: " + rs.getString("email"));
            System.out.println("--------------------");
        }

    } catch (java.sql.SQLException e) {
        System.out.println(e.getMessage());
    }
}

public void viewAllBookings() {

    String sql = "SELECT * FROM bookings";

    try (java.sql.Connection con = DBConnection.getConnection();
         java.sql.Statement st = con.createStatement();
         java.sql.ResultSet rs = st.executeQuery(sql)) {

        System.out.println("\n--- ALL BOOKINGS ---");

        while (rs.next()) {
            System.out.println("Booking ID: " + rs.getInt("booking_id"));
            System.out.println("Customer ID: " + rs.getInt("customer_id"));
            System.out.println("Room: " + rs.getInt("room_number"));
            System.out.println("Days: " + rs.getInt("days"));
            System.out.println("Bill: Rs. " + rs.getDouble("total_amount"));
            System.out.println("Status: " + rs.getString("status"));
            System.out.println("--------------------");
        }

    } catch (java.sql.SQLException e) {
        System.out.println(e.getMessage());
    }
}

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

    String sql = "UPDATE bookings SET status = 'Checked In' WHERE booking_id = ?";

    try (java.sql.Connection con = DBConnection.getConnection();
         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, bookingId);
        ps.executeUpdate();

        booking.checkIn();
        System.out.println("Check-in saved in MySQL!");

    } catch (java.sql.SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
}




                    




}

