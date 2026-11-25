

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        HotelManagement hotel = new HotelManagement();

        System.out.println("================================");
        System.out.println("     HOTEL MANAGEMENT SYSTEM");
        System.out.println("================================");

        System.out.print("Enter Admin Username: ");
        String username = sc.nextLine();

        System.out.print("Enter Admin Password: ");
        String password = sc.nextLine();

       Admin admin = new Admin("admin", "1234");

if (!admin.login(username, password)) {
            System.out.println("Invalid Login! Access Denied.");
            sc.close();
            return;
        }

        System.out.println("\nLogin Successful! Welcome Admin.");

        boolean running = true;

        while (running) {

            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Register Customer");
            System.out.println("3. Book a Room");
            System.out.println("4. View Customers");
            System.out.println("5. View All Bookings");
            System.out.println("6. Check-In");
            System.out.println("7. Check-Out and Generate Bill");
            System.out.println("8. Exit");
            System.out.println("===============================");

            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            try {

                switch (choice) {

                    case "1":
                        hotel.showAvailableRooms();
                        break;

                    case "2":
                        System.out.print("Enter Customer ID: ");
                        int id = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter Customer Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Phone Number: ");
                        String phone = sc.nextLine();

                        System.out.print("Enter Email: ");
                        String email = sc.nextLine();

                        Customer customer =
                            new Customer(id, name, phone, email);

                        hotel.registerCustomer(customer);
                        break;

                    case "3":
                        System.out.print("Enter Booking ID: ");
                        int bookingId = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter Customer ID: ");
                        int customerId = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter Room Number: ");
                        int roomNumber = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter Number of Days: ");
                        int days = Integer.parseInt(sc.nextLine());

                        hotel.bookRoom(
                            bookingId, customerId, roomNumber, days
                        );
                        break;

                    case "4":
    hotel.viewCustomers();
    break;

case "5":
    hotel.viewAllBookings();
    break;
                    case "6":
                        System.out.print("Enter Booking ID: ");
                        int checkInId =
                            Integer.parseInt(sc.nextLine());

                        hotel.checkIn(checkInId);
                        break;

                    case "7":
                        System.out.print("Enter Booking ID: ");
                        int checkOutId =
                            Integer.parseInt(sc.nextLine());

                        hotel.checkOut(checkOutId);
                        break;

                    case "8":
                        running = false;
                        System.out.println(
                            "Thank you for using our system!"
                        );
                        break;

                    default:
                        System.out.println(
                            "Invalid choice! Please try again."
                        );
                }

            } catch (NumberFormatException e) {
                System.out.println(
                    "Invalid input! Please enter numbers where required."
                );
            }
        }

        sc.close();
    }
}
