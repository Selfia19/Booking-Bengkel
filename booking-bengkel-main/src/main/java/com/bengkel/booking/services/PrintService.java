package com.bengkel.booking.services;

import java.text.DecimalFormat;
import java.util.List;

import com.bengkel.booking.models.Car;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;
import com.bengkel.booking.repositories.CustomerRepository;

public class PrintService {
	public static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();


	public static void setListAllCustomers(List<Customer> listAllCustomers) {
        PrintService.listAllCustomers = listAllCustomers;
    }

	public static void printMenu(String[] listMenu, String title) {
		String line = "+============================================================================+";
		int number = 1;
		String formatTable = " %-2s. %-25s %n";
		
		System.out.printf("%-25s %n", title);
		System.out.println(line);
		
		for (String data : listMenu) {
			if (number < listMenu.length) {
				System.out.printf(formatTable, number, data);
			}else {
				System.out.printf(formatTable, 0, data);
			}
			number++;
		}
		System.out.println(line);
		System.out.println();
	}
	

	public static void printVechicle(List<Vehicle> listVehicle) {
		String formatTable = "| %-2s | %-15s | %-10s | %-15s | %-15s | %-5s | %-15s |%n";
		String line = "+----+-----------------+------------+-----------------+-----------------+-------+-----------------+%n";
		System.out.format(line);
		System.out.format(formatTable, "No", "Vechicle Id", "Warna", "Brand", "Transmisi", "Tahun", "Tipe Kendaraan");
		System.out.format(line);
		int number = 1;
		String vehicleType = "";
		for (Vehicle vehicle : listVehicle) {
			if (vehicle instanceof Car) {
				vehicleType = "Mobil";
			}else {
				vehicleType = "Motor";
			}
			System.out.format(formatTable, number, vehicle.getVehiclesId(), vehicle.getColor(), vehicle.getBrand(), vehicle.getTransmisionType(), vehicle.getYearRelease(), vehicleType);
			number++;
		}
		System.out.printf(line);
	}


	public static void printCustomer(String customerId) {
    Customer loggedInCustomer = null;
    for (Customer customer : listAllCustomers) {
        if (customer.getCustomerId().equals(customerId)) {
            loggedInCustomer = customer;
            break;
        }
    }

	if (loggedInCustomer != null) {
		System.out.println("============================================================================");
		System.out.println("==================== Customer Profile ======================================");
		System.out.println("============================================================================");
		System.out.println("Status         : " + (loggedInCustomer instanceof MemberCustomer ? "Member" : "Non Member"));
		System.out.println("Customer Id    : " + loggedInCustomer.getCustomerId());
		System.out.println("Nama           : " + loggedInCustomer.getName());
		System.out.println("Alamat         : " + loggedInCustomer.getAddress());
		if (loggedInCustomer instanceof MemberCustomer) {
			MemberCustomer memberCustomer = (MemberCustomer) loggedInCustomer;
			double saldoKoin = memberCustomer.getSaldoCoin();
			DecimalFormat decimalFormat = new DecimalFormat("#,###");
			String formattedSaldoKoin = decimalFormat.format(saldoKoin);
			System.out.println("Saldo Koin     : " + formattedSaldoKoin);
		}
		
		printVehicleList(loggedInCustomer);

	} else {
		System.out.println("Customer tidak ditemukan.");
	}
	
	}

	public static void printVehicleList(Customer loggedInCustomer) {
		System.out.println("List Kendaraan :");
		System.out.format("| %-5s | %-10s | %-15s | %-20s | %-10s%n", "No", "Vehicle Id", "Warna", "Tipe Kendaraan", "Tahun");
		System.out.println("------------------------------------------------------------------------");
	
		List<Vehicle> vehicles = loggedInCustomer.getVehicles();
		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle vehicle = vehicles.get(i);
			System.out.format("| %-5d | %-10s | %-15s | %-20s | %-10d%n", i + 1, vehicle.getVehiclesId(), vehicle.getColor(), vehicle.getVehicleType(), vehicle.getYearRelease());
		}
	}
	

	public static void printItemServices(List<ItemService> listAllItemService, Vehicle selectedVehicle) {
        String line = "+==========================================================================+";
        System.out.println(line);
        System.out.printf("| %-10s | %-30s | %-10s |%n", "Service Id", "Service Name", "Price");
        System.out.println(line);

        for (ItemService itemService : listAllItemService) {
            if (itemService.getVehicleType().equals(selectedVehicle.getVehicleType())) {
                System.out.printf("| %-10s | %-30s | %-10.2f |%n", itemService.getServiceId(), itemService.getServiceName(), itemService.getPrice());
            }
        }
        
        System.out.println(line);
    }


	///////////////////////////
	// public static void displayBookingInfo(BookingOrder bookingOrder, String customerId) {
	// 	Customer loggedInCustomer = null;
	// 	for (Customer customer : listAllCustomers) {
	// 		if (customer.getCustomerId().equals(customerId)) {
	// 			loggedInCustomer = customer;
	// 			break;
	// 		}
	// 	}
	
	// 	if (loggedInCustomer != null) {
	// 	System.out.println("Booking Id: " + bookingOrder.getBookingId());
	// 	System.out.println("Nama Customer: " + bookingOrder.getCustomer().getName());
	// 	System.out.println("List Service:");
	// 	for (ItemService service : bookingOrder.getServices()) {
	// 		System.out.println("- " + service.getServiceName() + ": " + service.getPrice());
	// 	}
	// 	System.out.println("Payment Method: " + bookingOrder.getPaymentMethod());
	// 	System.out.println("Total Service Price: " + bookingOrder.getTotalServicePrice());
	// 	System.out.println("Total Payment: " + bookingOrder.getTotalPayment());
	// 	System.out.println("Booking Date: " + bookingOrder.getCreatedAt());
	// 	System.out.println("==================================");
	// }
	// }
	

	//Silahkan Tambahkan function print sesuai dengan kebutuhan.
	
}
