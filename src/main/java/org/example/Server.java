package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final CarDealership carDealership = new CarDealership();

    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Galeri sunucusu " + port + " portunda dinleniyor");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Sunucu hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static private class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String message;
                String greetingMessage = "Araba Galerisine Hoşgeldiniz!\n" +
                        "Kullanılabilir komutlar:\n" +
                        "LIST - Tüm araçları listele\n" +
                        "DETAILS <model> - Araç detaylarını göster (örn: DETAILS 'BMW 3 Series')\n" +
                        "BUY <model> <bütçe> - Araç satın al (örn: BUY 'Honda Civic' 30000)\n" +
                        "UPDATE <model> <yeni_fiyat> - Araç fiyatını güncelle\n" +
                        "RESTOCK - Galeri stoklarını yenile\n" +
                        "EXIT - Çıkış";

                output.println(greetingMessage);

                while ((message = input.readLine()) != null) {
                    String response = ProcessAndResponseToClientMessage(message);
                    output.println(response);

                    if (message.equalsIgnoreCase("EXIT")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("İstemci hatası: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("İstemci soketi kapatılırken hata: " + e.getMessage());
                }
            }
        }
    }

    private static String ProcessAndResponseToClientMessage(String message) {
        try {
            if (message.equalsIgnoreCase("LIST")) {
                return carDealership.displayCars();
            } else if (message.startsWith("BUY")) {
                // Mesajı parçalara ayır ve tırnak işaretlerini yönet
                String trimmedMessage = message.trim();
                int firstQuote = trimmedMessage.indexOf('\'');
                int lastQuote = trimmedMessage.lastIndexOf('\'');

                if (firstQuote != -1 && lastQuote != -1 && firstQuote != lastQuote) {
                    // Model adını tırnak işaretleri arasından al
                    String model = trimmedMessage.substring(firstQuote + 1, lastQuote);

                    // Bütçeyi son tırnaktan sonraki kısımdan al
                    String budgetStr = trimmedMessage.substring(lastQuote + 1).trim();

                    try {
                        double budget = Double.parseDouble(budgetStr);
                        return carDealership.buyCar(model, budget);
                    } catch (NumberFormatException e) {
                        return "Hata: Geçersiz bütçe formatı. Lütfen sayısal bir değer girin.";
                    }
                } else {
                    return "Geçersiz komut formatı. Kullanım: BUY 'model adı' bütçe";
                }
            } else if (message.startsWith("DETAILS")) {
                String[] parts = message.split(" ", 2);
                if (parts.length == 2) {
                    String model = parts[1].replace("'", "");
                    return carDealership.getCarDetails(model);
                } else {
                    return "Geçersiz komut. Kullanım: DETAILS '<model>'";
                }
            } else if (message.startsWith("UPDATE")) {
                // Mesajı parçalara ayır ve tırnak işaretlerini yönet
                String trimmedMessage = message.trim();
                int firstQuote = trimmedMessage.indexOf('\'');
                int lastQuote = trimmedMessage.lastIndexOf('\'');

                if (firstQuote != -1 && lastQuote != -1 && firstQuote != lastQuote) {
                    // Model adını tırnak işaretleri arasından al
                    String model = trimmedMessage.substring(firstQuote + 1, lastQuote);

                    // Yeni fiyatı son tırnaktan sonraki kısımdan al
                    String priceStr = trimmedMessage.substring(lastQuote + 1).trim();

                    try {
                        double newPrice = Double.parseDouble(priceStr);
                        carDealership.updateCarPrice(model, newPrice);
                        return "Araç fiyatı güncellendi: " + model + " - Yeni fiyat: $" + newPrice;
                    } catch (NumberFormatException e) {
                        return "Hata: Geçersiz fiyat formatı. Lütfen sayısal bir değer girin.";
                    }
                } else {
                    return "Geçersiz komut formatı. Kullanım: UPDATE 'model adı' yeni_fiyat";
                }
            } else if (message.equalsIgnoreCase("RESTOCK")) {
                carDealership.restockCars();
                return "Galeri stokları yenilendi.";
            } else if (message.equalsIgnoreCase("EXIT")) {
                return "Galeriyi ziyaret ettiğiniz için teşekkürler!";
            } else {
                return "Bilinmeyen komut. Yardım için LIST komutunu kullanın.";
            }
        } catch (Exception e) {
            return "Hata: " + e.getMessage();
        }
    }
}