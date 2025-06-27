package com.ncr.printer.examples;

import com.ncr.printer.Ncr7167Config;
import com.ncr.printer.Ncr7167Exception;
import com.ncr.printer.Ncr7167Printer;
import com.ncr.printer.commands.Ncr7167Commands;
import com.ncr.printer.util.ReceiptBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Basit yazıcı testi için minimal örnek
 */
public class SimpleExample {
    
    public static void main(String[] args) {
        // Konfigürasyon
        Ncr7167Config config = new Ncr7167Config();
        config.setPortName("COM1"); // Linux için /dev/ttyUSB0
        config.setBaudRate(9600);
        
        // Yazıcı oluşturma
        Ncr7167Printer printer = new Ncr7167Printer(config);
        
        try {
            System.out.println("NCR 7167 yazıcısına bağlanılıyor...");
            
            // Bağlantı kurma
            printer.connect();
            System.out.println("Bağlantı başarılı!");
            
            // Yazıcı başlatma
            printer.initialize();
            
            // Receipt istasyonu seçme
            printer.selectReceiptStation();
            
            // Basit test yazdırma
            printer.printLine("=== NCR 7167 TEST ===");
            printer.printLine("");
            printer.printLine("Tarih: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            printer.printLine("Port: " + config.getPortName());
            printer.printLine("");
            
            // Farklı formatlama örnekleri
            printer.setEmphasized(true);
            printer.printLine("KALIN METİN");
            printer.setEmphasized(false);
            
            printer.setUnderline(true);
            printer.printLine("Altı çizili metin");
            printer.setUnderline(false);
            
            printer.setDoubleWide(true);
            printer.printLine("ÇİFT GENİŞLİK");
            printer.setDoubleWide(false);
            
            printer.printLine("");
            printer.printLine("Test başarılı!");
            
            // Kağıt besleme
            printer.feedPaper(3);
            
            // Kağıt kesme (varsa)
            try {
                printer.cutPaper();
                System.out.println("Kağıt kesildi");
            } catch (Exception e) {
                System.out.println("Kağıt kesme özelliği mevcut değil");
            }
            
            System.out.println("Test tamamlandı!");
            
        } catch (Ncr7167Exception e) {
            System.err.println("Yazıcı hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            printer.disconnect();
            System.out.println("Yazıcı bağlantısı kesildi");
        }
    }
}
