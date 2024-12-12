package org.example;

import java.util.HashMap;
import java.util.Map;

class CarDealership {
    private Map<String, Integer> cars;
    private Map<String, Double> prices;

    public CarDealership() {
        cars = new HashMap<String, Integer>();
        prices = new HashMap<String, Double>();

        // Model isimleri tam olarak client'ın göndereceği formatta olmalı
        addCar("Honda Civic", 3, 27000.0);  // BUY 'Honda Civic' 27000 şeklinde gelen istekle eşleşmeli
        addCar("Toyota Corolla", 5, 25000.0);
        addCar("BMW 3 Series", 2, 45000.0);
        addCar("Mercedes C-Class", 2, 48000.0);
        addCar("Volkswagen Golf", 4, 23000.0);
        addCar("Audi A3", 3, 35000.0);
        addCar("Ford Focus", 5, 22000.0);
        addCar("Hyundai i20", 6, 19000.0);
        addCar("Renault Clio", 4, 20000.0);
        addCar("Fiat Egea", 5, 18000.0);
    }

    private void addCar(String model, int count, double price) {
        cars.put(model, count);
        prices.put(model, price);
    }

    public String buyCar(String carModel, double budget) {
        // Debug için yazdırma
        System.out.println("Aranan model: '" + carModel + "'");
        System.out.println("Mevcut modeller: " + cars.keySet());

        if (cars.containsKey(carModel) && cars.get(carModel) > 0) {
            if (budget - prices.get(carModel) < 0) {
                return "Bütçeniz " + carModel + " için yeterli değil.\n" +
                        "Araç fiyatı: $" + prices.get(carModel) + "\n" +
                        "Bütçeniz: $" + budget + "\n";
            }
            cars.put(carModel, cars.get(carModel) - 1);
            return "Satın alınan araç: " + carModel + "\n" +
                    "Kalan bütçe: $" + (budget - prices.get(carModel)) + "\n" +
                    "İşlem başarıyla tamamlandı!\n";
        } else {
            return "Bu model araç stokta yok veya tükenmiş.\n";
        }
    }

    public void restockCars() {
        for (String car : cars.keySet()) {
            cars.replace(car, 5);
        }
    }

    public String displayCars() {
        StringBuilder sb = new StringBuilder();
        sb.append("Galerideki Araçlar:\n");
        sb.append("==========================================\n");
        for (String car : cars.keySet()) {
            sb.append(String.format("%-20s - $%-10.2f (Stok: %d)\n",
                    car, prices.get(car), cars.get(car)));
        }
        sb.append("==========================================\n");
        return sb.toString();
    }

    public String getCarDetails(String model) {
        if (cars.containsKey(model)) {
            return String.format("Model: %s\n" +
                            "Fiyat: $%.2f\n" +
                            "Stok Durumu: %d adet\n",
                    model, prices.get(model), cars.get(model));
        }
        return "Araç bulunamadı.\n";
    }

    public void updateCarPrice(String model, double newPrice) {
        if (prices.containsKey(model)) {
            prices.put(model, newPrice);
        }
    }
}