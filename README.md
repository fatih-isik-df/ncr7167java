# NCR 7167 Java Library

Bu Java kütüphanesi, NCR 7167 Two-Station POS Yazıcısı ile iletişim kurmak için geliştirilmiştir. NCR 7167 Owner's Manual belgesi baz alınarak hazırlanmıştır.

## Özellikler

- RS-232C ve USB iletişim desteği
- Receipt (Fiş) ve Slip (Form/Çek) istasyonu desteği
- Metin formatlama (kalın, altı çizili, çift genişlik)
- Barkod yazdırma desteği
- Para çekmeci kontrolü
- Kağıt kesme işlevi
- Printer durumu sorgulama
- Kolay kullanım için ReceiptBuilder sınıfı

## Desteklenen Yazıcı Komutları

### Temel Komutlar
- Yazıcı başlatma
- Buffer temizleme
- İstasyon seçimi (Receipt/Slip)
- Metin yazdırma ve satır besleme

### Metin Formatlama
- Kalın (Emphasized) metin
- Altı çizili metin
- Çift genişlik karakterler
- Satır aralığı ayarlama
- Karakter pitch ayarlama

### Barkod Yazdırma
- UPC-A, UPC-E
- Code 39, Code 93, Code 128
- JAN/EAN-8, JAN/EAN-13
- Interleaved 2 of 5
- Codabar
- PDF417

### Diğer Özellikler
- Para çekmeci açma
- Kağıt kesme (tam/kısmi)
- Yazıcı durumu kontrolü
- Kağıt varlığı/azlığı kontrolü

## Kurulum

### Maven Dependency

```xml
<dependency>
    <groupId>com.ncr</groupId>
    <artifactId>ncr7167-java</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Gereksinimler
- Java 11 veya üzeri
- JSerialComm kütüphanesi (otomatik yüklenecek)

## Kullanım

### Temel Kullanım

```java
import com.ncr.printer.Ncr7167Config;
import com.ncr.printer.Ncr7167Printer;
import com.ncr.printer.Ncr7167Exception;

// Yazıcı konfigürasyonu
Ncr7167Config config = new Ncr7167Config();
config.setPortName("COM1"); // Windows için COM1, Linux için /dev/ttyUSB0
config.setBaudRate(9600);
config.setFlowControl(Ncr7167Config.FlowControl.XON_XOFF);

// Yazıcı nesnesi oluşturma
Ncr7167Printer printer = new Ncr7167Printer(config);

try {
    // Bağlantı kurma
    printer.connect();
    
    // Yazıcıyı başlatma
    printer.initialize();
    
    // Receipt istasyonunu seçme
    printer.selectReceiptStation();
    
    // Basit metin yazdırma
    printer.printLine("Merhaba Dünya!");
    
    // Kağıt besleme ve kesme
    printer.feedPaper(3);
    printer.cutPaper();
    
} catch (Ncr7167Exception e) {
    System.err.println("Yazıcı hatası: " + e.getMessage());
} finally {
    printer.disconnect();
}
```

### Fiş Yazdırma (ReceiptBuilder ile)

```java
import com.ncr.printer.util.ReceiptBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

ReceiptBuilder receipt = new ReceiptBuilder(printer);
receipt
    .header("MAĞAZA ADI", "Adres Bilgisi")
    .emptyLine()
    .item("Ürün 1", "10.50 TL")
    .item("Ürün 2", "25.00 TL")
    .item("Ürün 3", "5.75 TL")
    .separator()
    .item("Ara Toplam", "41.25 TL")
    .item("KDV", "7.43 TL")
    .total("TOPLAM", "48.68 TL")
    .emptyLine()
    .centerLine("*** NAKİT ÖDEMELİ ***")
    .footer("TXN001234", dateTime)
    .complete();
```

### Barkod Yazdırma

```java
import com.ncr.printer.commands.Ncr7167Commands;

// Barkod yüksekliği ayarlama
printer.setBarCodeHeight(50);

// Code 39 barkod yazdırma
printer.printBarCode(Ncr7167Commands.BarCodeTypes.CODE39, "123456789");

// Code 128 barkod yazdırma
printer.printBarCode(Ncr7167Commands.BarCodeTypes.CODE128, "HELLO123");

// UPC-A barkod yazdırma
printer.printBarCode(Ncr7167Commands.BarCodeTypes.UPC_A, "012345678905");
```

### Metin Formatlama

```java
// Kalın metin
printer.setEmphasized(true);
printer.printLine("KALIN METİN");
printer.setEmphasized(false);

// Altı çizili metin
printer.setUnderline(true);
printer.printLine("Altı çizili metin");
printer.setUnderline(false);

// Çift genişlik
printer.setDoubleWide(true);
printer.printLine("ÇİFT GENİŞLİK");
printer.setDoubleWide(false);
```

### Para Çekmeci Kontrolü

```java
// Varsayılan zamanlama ile çekmece açma (110ms)
printer.openCashDrawer();

// Özel zamanlama ile çekmece açma
printer.openCashDrawer(55, 55); // 55*2ms = 110ms açık kalma süresi
```

### Yazıcı Durumu Kontrolü

```java
// Yazıcı durumu sorgulama
int status = printer.requestStatus();

// Kağıt durumu kontrolü
boolean paperPresent = printer.isPaperPresent();
boolean paperLow = printer.isPaperLow();

System.out.println("Kağıt var: " + paperPresent);
System.out.println("Kağıt az: " + paperLow);
```

### Slip İstasyonu Kullanımı

```java
// Slip istasyonunu seçme
printer.selectSlipStation();

// Form/çek yazdırma
printer.printLine("Çek Onayı");
printer.printLine("Tarih: " + LocalDate.now());
printer.printLine("Tutar: 1000.00 TL");

// Receipt istasyonuna geri dönme
printer.selectReceiptStation();
```

## İletişim Konfigürasyonu

### RS-232C Ayarları

```java
Ncr7167Config config = new Ncr7167Config();
config.setPortName("COM1");
config.setBaudRate(9600); // 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200
config.setDataBits(8); // 7 veya 8
config.setStopBits(1); // 1 veya 2
config.setParity(0); // 0=None, 1=Even, 2=Odd
config.setFlowControl(Ncr7167Config.FlowControl.XON_XOFF); // veya DTR_DSR
config.setTimeout(5000); // milisaniye
```

### USB Ayarları

USB bağlantısı için yazıcının sanal COM port olarak tanımlanması gerekir. Windows'ta NCR USB sürücüleri yüklendikten sonra yazıcı COM port olarak görünecektir.

```java
config.setInterfaceType(Ncr7167Config.InterfaceType.USB);
config.setPortName("COM3"); // USB'nin atandığı COM port
```

## Mevcut COM Portlarını Listeleme

```java
String[] ports = Ncr7167Printer.getAvailablePorts();
for (String port : ports) {
    System.out.println("Mevcut port: " + port);
}
```

## Örnek Uygulama Çalıştırma

```bash
mvn compile exec:java -Dexec.mainClass="com.ncr.printer.examples.PrinterExample"
```

Bu komut örnek uygulamayı çalıştırır ve interaktif menü sunar:
1. Self test
2. Örnek fiş yazdırma
3. Barkod testi
4. Metin formatlama testi
5. Yazıcı durumu kontrolü
6. Para çekmeci testi
7. Slip istasyonu testi
8. Özel metin yazdırma

## Test Etme

```bash
# Tüm testleri çalıştırma
mvn test

# Belirli bir test sınıfını çalıştırma
mvn test -Dtest=Ncr7167ConfigTest
```

## Hata Yönetimi

Tüm yazıcı işlemleri `Ncr7167Exception` fırlatabilir. Bu exception'ı yakalayarak hataları ele alın:

```java
try {
    printer.printLine("Test");
} catch (Ncr7167Exception e) {
    System.err.println("Yazıcı hatası: " + e.getMessage());
    e.printStackTrace();
}
```

## Loglama

Kütüphane SLF4J kullanır. Detaylı logları görmek için logback konfigürasyonu:

```xml
<!-- src/main/resources/logback.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.ncr.printer" level="DEBUG"/>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

## Desteklenen Yazıcı Modelleri

Bu kütüphane NCR 7167 serisindeki yazıcılar için geliştirilmiştir:
- NCR 7167-1xxx serisi
- NCR 7167-2xxx serisi

## Sorun Giderme

### Bağlantı Sorunları
1. COM port adının doğru olduğundan emin olun
2. Başka bir uygulama portu kullanıyor olabilir
3. Yazıcının açık ve bağlı olduğunu kontrol edin
4. USB yazıcılar için sürücülerin yüklü olduğunu kontrol edin

### Yazdırma Sorunları
1. Kağıt durumunu kontrol edin (`isPaperPresent()`)
2. Yazıcı kapağının kapalı olduğundan emin olun
3. Doğru istasyonun seçildiğini kontrol edin
4. Flow control ayarlarını kontrol edin

### Performans İyileştirmeleri
1. Sık kullanılan komutları buffer'layın
2. Gereksiz bağlantı açma/kapama işlemlerinden kaçının
3. Yüksek baud rate kullanın (19200 veya 38400)

## Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/AmazingFeature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some AmazingFeature'`)
4. Branch'e push edin (`git push origin feature/AmazingFeature`)
5. Pull Request oluşturun

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## İletişim

Sorularınız için issue oluşturun veya pull request gönderin.
