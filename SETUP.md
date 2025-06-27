# NCR 7167 Java Library - Kurulum ve Kullanım Kılavuzu

## Proje Yapısı

```
ncr7167java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ncr/printer/
│   │   │       ├── Ncr7167Printer.java          # Ana yazıcı sınıfı
│   │   │       ├── Ncr7167Config.java           # Konfigürasyon sınıfı
│   │   │       ├── Ncr7167Exception.java        # Exception sınıfı
│   │   │       ├── commands/
│   │   │       │   └── Ncr7167Commands.java     # Yazıcı komutları
│   │   │       ├── util/
│   │   │       │   └── ReceiptBuilder.java      # Fiş oluşturucu
│   │   │       └── examples/
│   │   │           ├── PrinterExample.java      # Detaylı örnek
│   │   │           └── SimpleExample.java       # Basit örnek
│   │   └── resources/
│   │       └── logback.xml                      # Log konfigürasyonu
│   └── test/
│       └── java/
│           └── com/ncr/printer/
│               ├── Ncr7167ConfigTest.java       # Konfigürasyon testleri
│               └── commands/
│                   └── Ncr7167CommandsTest.java # Komut testleri
├── pom.xml                                      # Maven konfigürasyonu
└── README.md                                    # Dokümantasyon
```

## Hızlı Başlangıç

### 1. Projeyi Derleme

```bash
mvn clean compile
```

### 2. Testleri Çalıştırma

```bash
mvn test
```

### 3. Basit Örneği Çalıştırma

```bash
mvn compile exec:java -Dexec.mainClass="com.ncr.printer.examples.SimpleExample"
```

### 4. Detaylı Örneği Çalıştırma

```bash
mvn compile exec:java -Dexec.mainClass="com.ncr.printer.examples.PrinterExample"
```

## Önemli Notlar

### COM Port Konfigürasyonu

- **Windows**: `COM1`, `COM2`, `COM3`, vb.
- **Linux**: `/dev/ttyUSB0`, `/dev/ttyS0`, vb.
- **macOS**: `/dev/cu.usbserial-xxx`

### Desteklenen Baud Rate'ler

NCR 7167 manual'ına göre desteklenen baud rate'ler:
- 1200 bps
- 2400 bps
- 4800 bps
- 9600 bps (varsayılan)
- 19200 bps
- 38400 bps
- 57600 bps
- 115200 bps

### Flow Control Seçenekleri

1. **XON/XOFF** (Software) - Önerilen
2. **DTR/DSR** (Hardware)
3. **None** (Akış kontrolü yok)

## Kullanım Örnekleri

### Temel Kullanım

```java
// Yazıcı konfigürasyonu
Ncr7167Config config = new Ncr7167Config();
config.setPortName("COM1");

// Yazıcı nesnesi
Ncr7167Printer printer = new Ncr7167Printer(config);

try {
    printer.connect();
    printer.initialize();
    printer.selectReceiptStation();
    printer.printLine("Merhaba Dünya!");
    printer.feedPaper(2);
} finally {
    printer.disconnect();
}
```

### Fiş Yazdırma

```java
ReceiptBuilder receipt = new ReceiptBuilder(printer);
receipt
    .header("MAĞAZA ADI", "Adres")
    .item("Ürün 1", "10.50 TL")
    .item("Ürün 2", "25.00 TL")
    .total("TOPLAM", "35.50 TL")
    .footer("TXN123", "01/01/2024 10:30")
    .complete();
```

### Barkod Yazdırma

```java
// Barkod yüksekliği ayarla
printer.setBarCodeHeight(50);

// Code 39 barkod yazdır
printer.printBarCode(Ncr7167Commands.BarCodeTypes.CODE39, "123456789");
```

## Sorun Giderme

### Yaygın Hatalar

1. **"Port not found"**
   - COM port adını kontrol edin
   - Yazıcının bağlı olduğundan emin olun

2. **"Connection timeout"**
   - Baud rate'i kontrol edin
   - Flow control ayarlarını kontrol edin

3. **"No response from printer"**
   - Yazıcının açık olduğundan emin olun
   - Kablo bağlantılarını kontrol edin

### Debug Modu

Detaylı logları görmek için:

```java
// logback.xml dosyasında log seviyesini DEBUG yapın
<logger name="com.ncr.printer" level="DEBUG"/>
```

## Yazıcı Manuel Referansları

Bu implementasyon NCR 7167 Owner's Manual (B005-000-, Revision C, November 2003) belgesine dayanmaktadır.

### Temel Komutlar

- `0x10` - Clear Printer
- `0x0A` - Print and Feed One Line
- `0x1B 0x40` - Initialize Printer
- `0x1E` - Select Receipt Station
- `0x1C` - Select Slip Station

### ESC Komutları

- `ESC @` (1B 40) - Initialize Printer
- `ESC !` (1B 21) - Select Print Modes
- `ESC E` (1B 45) - Select Emphasized
- `ESC -` (1B 2D) - Select Underline

### GS Komutları

- `GS V` (1D 56) - Cut Paper
- `GS k` (1D 6B) - Print Bar Code
- `GS h` (1D 68) - Set Bar Code Height

## Geliştirme

### Yeni Özellik Ekleme

1. `Ncr7167Commands.java` dosyasına yeni komutları ekleyin
2. `Ncr7167Printer.java` dosyasına yeni metodları ekleyin
3. Test sınıfları oluşturun
4. Dokümantasyonu güncelleyin

### Test Ekleme

```java
@Test
void testNewFeature() {
    // Test kodları
}
```

Bu implementasyon, NCR 7167 yazıcısının tüm temel özelliklerini desteklemekte ve manuel belgede belirtilen komutları Java'da kullanılabilir hale getirmektedir.
