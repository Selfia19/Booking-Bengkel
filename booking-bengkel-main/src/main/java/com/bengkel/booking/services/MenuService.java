package com.bengkel.booking.services;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.bengkel.booking.interfaces.IBengkelPayment;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class MenuService {
    private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
    private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static int failedLoginAttempts = 0;
    private static Scanner input = new Scanner(System.in);
    private static String loggedInCustomerId;

	

    public static void run() {
        boolean isLooping = true;
        do {
            printWelcomeMenu();
            int choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 0:
                    isLooping = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (isLooping);
    }


    public static void login() {
        System.out.println("============================================================================");
        System.out.println("                 Aplikasi Booking Bengkel | LOGIN                 ");
        System.out.println("============================================================================");
        System.out.print("Masukkan Customer ID: ");
        String customerId = input.nextLine();
        System.out.print("Masukkan Password: ");
        String password = input.nextLine();

        boolean customerFound = false;

        for (Customer customer : listAllCustomers) {
            if (customer.getCustomerId().equals(customerId)) {
                customerFound = true;
                if (customer.getPassword().equals(password)) {
                    loggedInCustomerId = customerId;
                    mainMenu();
                    return;
                } else {
                    System.out.println("Password tidak benar.");
                    failedLoginAttempts++;
                    checkMaxLoginAttempts();
                    return;
                }
            }
        }

        if (!customerFound) {
            System.out.println("Customer ID tidak terdaftar.");
        }

        failedLoginAttempts++;
        checkMaxLoginAttempts();
        loggedInCustomerId = null;
    }


    private static void checkMaxLoginAttempts() {
        if (failedLoginAttempts >= MAX_LOGIN_ATTEMPTS) {
            System.out.println("Anda telah mencapai batas percobaan login yang gagal. Aplikasi akan keluar.");
            System.exit(0);
        }
    }


    public static void mainMenu() {
        String[] listMenu = {"Informasi Customer", "Booking Bengkel", "Top Up Bengkel Coin", "Informasi Booking", "Logout"};
        int menuChoice = 0;
        boolean isLooping = true;

        do {
            printMenuTable(listMenu, "Booking Bengkel Menu");
            menuChoice = Validation.validasiNumberWithRange("Masukan Pilihan Menu:", "Input Harus Berupa Angka!", "^[0-9]+$", listMenu.length, 0);
            System.out.println(menuChoice);

            switch (menuChoice) {
                case 1:
                    if (auth()) {
                        PrintService.printCustomer(loggedInCustomerId);
                    }
                    break;
                case 2:
                    if (auth()) {
                        bookServices(loggedInCustomerId);
                    }
                    break;

				case 3:
					if (auth()) {
						Customer loggedInCustomer = null;
						for (Customer customer : listAllCustomers) {
							if (customer.getCustomerId().equals(loggedInCustomerId)) {
								loggedInCustomer = customer;
								break;
							}
						}
						
						if (loggedInCustomer instanceof MemberCustomer) {
							topUpSaldoCoin(loggedInCustomerId);
						} else {
							System.out.println("Maaf, fitur ini hanya untuk member saja.");
						}
					}
					break;
				
                case 4:
					// if (auth()) {
					// 	// Mencari BookingOrder berdasarkan customerId
					// 	BookingOrder bookingOrder = findBookingOrderByCustomerId(loggedInCustomerId);
					// 	if (bookingOrder != null) {
					// 		PrintService.displayBookingInfo(bookingOrder, loggedInCustomerId);
					// 	} else {
					// 		System.out.println("Tidak ada pesanan yang ditemukan untuk customerId ini.");
					// 	}
					// }
					break;
                case 5:
                default:
                    if (auth()) {
                        logout();
                    }
                    isLooping = false;
                    break;
            }
        } while (isLooping);
    }


    public static void printMenuTable(String[] menuItems, String title) {
        System.out.println("===============================================");
        System.out.printf("%20s%n", title);
        System.out.println("===============================================");
        System.out.printf("%-5s | %s%n", "No", "Menu");
        System.out.println("-----------------------------------------------");
        for (int i = 0; i < menuItems.length; i++) {
            System.out.printf("%-5d | %s%n", i + 1, menuItems[i]);
        }
        System.out.println("===============================================");
    }


    public static boolean auth() {
        if (loggedInCustomerId != null) {
            System.out.println("Login sebagai : " + loggedInCustomerId);
            return true;
        } else {
            System.out.println("Lakukan Login terlebih dahulu.");
            return false;
        }
    }


    public static void printWelcomeMenu() {
        System.out.println("============================================================================");
        System.out.println("                 Aplikasi Booking Bengkel                         ");
        System.out.println("============================================================================");
        System.out.println("1. Login");
        System.out.println("0. Exit");
        System.out.println("============================================================================");
        System.out.print("Pilihan: ");
    }


    public static void logout() {
        loggedInCustomerId = null;
        System.out.println("Logout berhasil.");
    }


    public static void bookServices(String customerId) {
        Customer loggedInCustomer = null;
        for (Customer customer : listAllCustomers) {
            if (customer.getCustomerId().equals(customerId)) {
                loggedInCustomer = customer;
                break;
            }
        }

        if (loggedInCustomer != null) {

            List<Vehicle> customerVehicles = loggedInCustomer.getVehicles();

            PrintService.printVehicleList(loggedInCustomer);

            System.out.print("Masukkan Vehicle Id untuk memilih kendaraan: ");
            String vehicleId = input.nextLine();

            Vehicle selectedVehicle = null;
            for (Vehicle vehicle : customerVehicles) {
                if (vehicle.getVehiclesId().equals(vehicleId)) {
                    selectedVehicle = vehicle;
                    break;
                }
            }

            if (selectedVehicle == null) {
                System.out.println("Notifikasi Pesan: Kendaraan Tidak Ditemukan.");
                return;
            }

            PrintService.printItemServices(listAllItemService, selectedVehicle);

			if (selectedVehicle != null) {

			boolean continueBooking = true;
			List<ItemService> selectedServices = new ArrayList<>();
			int remainingServices = loggedInCustomer.getMaxNumberOfService();
			while (continueBooking && remainingServices > 0) {
				PrintService.printItemServices(listAllItemService, selectedVehicle);

				System.out.print("Masukkan Service Id untuk memilih layanan: ");
				String serviceId = input.nextLine();
				ItemService selectedService = null;
				for (ItemService itemService : listAllItemService) {
					if (itemService.getServiceId().equals(serviceId)) {
						selectedService = itemService;
						selectedServices.add(selectedService);
						System.out.print("Apakah Anda ingin memesan layanan lain? (Y/N): ");
						String continueChoice = input.nextLine().toUpperCase();
						if (!continueChoice.equals("Y")) {
							continueBooking = false;
						}
						break;
					}
				}

				if (selectedService == null) {
					System.out.println("Notifikasi Pesan: Layanan Tidak Ditemukan.");
					return;
				}

				if (loggedInCustomer instanceof MemberCustomer) {
					remainingServices--;
				}else{
					break;
				}
			}

			double totalPayment = 0;
			for (ItemService service : selectedServices) {
				totalPayment += service.getPrice();
			}

			System.out.println("Pilih Metode Pembayaran:");
			System.out.println("1. Tunai");
			if (loggedInCustomer instanceof MemberCustomer) {
				System.out.println("2. Saldo Koin");
			}
			System.out.print("Masukkan nomor metode pembayaran: ");
			int paymentMethod = input.nextInt();
			input.nextLine();

			double discountRate = (paymentMethod == 2 && loggedInCustomer instanceof MemberCustomer) ? IBengkelPayment.RATES_DISCOUNT_SALDO_COIN : IBengkelPayment.RATES_DISCOUNT_CASH;
			double discountAmount = totalPayment * discountRate;
			double finalPayment = totalPayment - discountAmount;

			System.out.println("Ringkasan Pesanan:");
			System.out.println("Vehicle Id: " + selectedVehicle.getVehiclesId());
			System.out.println("Total Pembayaran Sebelum Diskon: " + totalPayment);
			System.out.println("Diskon yang Diberikan: " + (discountRate * 100) + "%");
			System.out.println("Jumlah Pembayaran Akhir: " + finalPayment);

			if (loggedInCustomer instanceof MemberCustomer && paymentMethod == 2) {
				double saldoCoinPelanggan = ((MemberCustomer) loggedInCustomer).getSaldoCoin();
				if (saldoCoinPelanggan < finalPayment) {
					System.out.println("Notifikasi Pesan: Saldo koin tidak mencukupi untuk pembayaran.");
					return;
				}
	
				saldoCoinPelanggan -= finalPayment;
				((MemberCustomer) loggedInCustomer).setSaldoCoin(saldoCoinPelanggan);
				System.out.println("Saldo Koin Anda Sekarang: " + ((MemberCustomer) loggedInCustomer).getSaldoCoin());

				listAllCustomers = CustomerRepository.getAllCustomer();

				PrintService.setListAllCustomers(listAllCustomers);

				for (Customer customer : listAllCustomers) {
					if (customer instanceof MemberCustomer && customer.getCustomerId().equals(loggedInCustomer.getCustomerId())) {
						MemberCustomer memberCustomer = (MemberCustomer) customer;
						memberCustomer.setSaldoCoin(saldoCoinPelanggan);
						break;
					}
				}
	
                // BookingOrder bookingOrder = new BookingOrder(UUID.randomUUID().toString(), loggedInCustomer, selectedServices, (paymentMethod == 2 ? "Saldo Coin" : "Tunai"), totalPayment, finalPayment);

			}
				}
			}
		}
	


	public static void topUpSaldoCoin(String customerId) {
		Customer loggedInCustomer = null;
		for (Customer customer : listAllCustomers) {
			if (customer.getCustomerId().equals(customerId)) {
				loggedInCustomer = customer;
				break;
			}
		}
	
		if (loggedInCustomer != null) {
			if (loggedInCustomer instanceof MemberCustomer) {
				MemberCustomer memberCustomer = (MemberCustomer) loggedInCustomer;
				try {
					System.out.print("Masukkan jumlah saldo yang ingin ditambahkan: ");
					double amount = input.nextDouble();
					input.nextLine();
	
					if (amount <= 0) {
						System.out.println("Jumlah saldo yang dimasukkan harus lebih besar dari 0.");
					} else {
						memberCustomer.setSaldoCoin(memberCustomer.getSaldoCoin() + amount);
						System.out.println("Saldo Koin Anda Sekarang: " + memberCustomer.getSaldoCoin());
					}
				} catch (InputMismatchException e) {
					System.out.println("Input yang dimasukkan bukan angka.");
					input.nextLine();
				}
			} else {
				System.out.println("Anda harus menjadi member untuk melakukan top-up saldo coin.");
			}
		} else {
			System.out.println("Customer dengan ID tersebut tidak ditemukan.");
		}
	}
	

	// public BookingOrder findBookingOrderByCustomerId(String customerId, List<Customer> customers) {
	// 	for (Customer customer : customers) {
	// 		for (BookingOrder bookingOrder : customer.getBookingOrders()) {
	// 			if (bookingOrder.getCustomer().getCustomerId().equals(customerId)) {
	// 				return bookingOrder;
	// 			}
	// 		}
	// 	}
	// 	return null;
	// }
	

}

