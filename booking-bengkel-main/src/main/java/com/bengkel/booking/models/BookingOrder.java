package com.bengkel.booking.models;

import java.time.LocalDateTime;
import java.util.List;

import com.bengkel.booking.interfaces.IBengkelPayment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingOrder implements IBengkelPayment {
    private String bookingId;
    private Customer customer;
    private List<ItemService> services;
    private String paymentMethod;
    private double totalServicePrice;
    private double totalPayment;
    private LocalDateTime createdAt;

    public void calculatePayment() {
        double discount = 0;
        if (paymentMethod.equalsIgnoreCase("Saldo Coin")) {
            discount = getTotalServicePrice() * IBengkelPayment.RATES_DISCOUNT_SALDO_COIN;
        } else {
            discount = getTotalServicePrice() * IBengkelPayment.RATES_DISCOUNT_CASH;
        }

        setTotalPayment(getTotalServicePrice() - discount);
    }

    @Override
    public void processPayment(double amount) {
        // Implementasi logika untuk memproses pembayaran
    }
}

