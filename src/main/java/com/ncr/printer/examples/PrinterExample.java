package com.ncr.printer.examples;

import com.ncr.printer.Ncr7167Config;
import com.ncr.printer.Ncr7167Exception;
import com.ncr.printer.Ncr7167Printer;
import com.ncr.printer.commands.Ncr7167Commands;
import com.ncr.printer.util.ReceiptBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Example application demonstrating NCR 7167 printer usage
 */
public class PrinterExample {
    
    public static void main(String[] args) {
        // Show available ports
        System.out.println("Available COM ports:");
        String[] ports = Ncr7167Printer.getAvailablePorts();
        for (int i = 0; i < ports.length; i++) {
            System.out.println((i + 1) + ". " + ports[i]);
        }
        
        if (ports.length == 0) {
            System.out.println("No COM ports found!");
            return;
        }
        
        // Let user select a port
        Scanner scanner = new Scanner(System.in);
        System.out.print("Select port number (1-" + ports.length + "): ");
        
        try {
            int selection = Integer.parseInt(scanner.nextLine()) - 1;
            if (selection < 0 || selection >= ports.length) {
                System.out.println("Invalid selection!");
                return;
            }
            
            // Configure printer
            Ncr7167Config config = new Ncr7167Config();
            config.setPortName(ports[selection]);
            config.setBaudRate(9600);
            config.setFlowControl(Ncr7167Config.FlowControl.XON_XOFF);
            
            // Create printer instance
            Ncr7167Printer printer = new Ncr7167Printer(config);
            
            System.out.println("Connecting to printer on " + config.getPortName() + "...");
            
            try {
                // Connect to printer
                printer.connect();
                System.out.println("Connected successfully!");
                
                // Show menu
                showMenu();
                
                while (true) {
                    System.out.print("Enter choice (1-8, 0 to exit): ");
                    String choice = scanner.nextLine();
                    
                    switch (choice) {
                        case "0":
                            System.out.println("Exiting...");
                            return;
                            
                        case "1":
                            performSelfTest(printer);
                            break;
                            
                        case "2":
                            printSampleReceipt(printer);
                            break;
                            
                        case "3":
                            testBarCode(printer);
                            break;
                            
                        case "4":
                            testFormatting(printer);
                            break;
                            
                        case "5":
                            checkPrinterStatus(printer);
                            break;
                            
                        case "6":
                            testCashDrawer(printer);
                            break;
                            
                        case "7":
                            testSlipStation(printer);
                            break;
                            
                        case "8":
                            System.out.print("Enter text to print: ");
                            String text = scanner.nextLine();
                            printer.printLine(text);
                            printer.feedPaper(2);
                            break;
                            
                        default:
                            System.out.println("Invalid choice!");
                            showMenu();
                    }
                }
                
            } catch (Ncr7167Exception e) {
                System.err.println("Printer error: " + e.getMessage());
                e.printStackTrace();
                
            } finally {
                printer.disconnect();
                System.out.println("Disconnected from printer");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
        } finally {
            scanner.close();
        }
    }
    
    private static void showMenu() {
        System.out.println("\n=== NCR 7167 Printer Test Menu ===");
        System.out.println("1. Perform self test");
        System.out.println("2. Print sample receipt");
        System.out.println("3. Test bar codes");
        System.out.println("4. Test text formatting");
        System.out.println("5. Check printer status");
        System.out.println("6. Test cash drawer");
        System.out.println("7. Test slip station");
        System.out.println("8. Print custom text");
        System.out.println("0. Exit");
        System.out.println("=====================================");
    }
    
    private static void performSelfTest(Ncr7167Printer printer) {
        try {
            System.out.println("Performing self test...");
            printer.performSelfTest();
            System.out.println("Self test completed!");
            
        } catch (Ncr7167Exception e) {
            System.err.println("Self test failed: " + e.getMessage());
        }
    }
    
    private static void printSampleReceipt(Ncr7167Printer printer) {
        try {
            System.out.println("Printing sample receipt...");
            
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            
            ReceiptBuilder receipt = new ReceiptBuilder(printer);
            receipt
                .header("SAMPLE STORE", "123 Main Street")
                .emptyLine()
                .item("Apple Juice", "$2.50")
                .item("Bread", "$1.25")
                .item("Milk 1L", "$3.00")
                .item("Coffee", "$4.50")
                .separator()
                .item("Subtotal", "$11.25")
                .item("Tax", "$1.13")
                .total("TOTAL", "$12.38")
                .emptyLine()
                .centerLine("*** PAID BY CASH ***")
                .item("Cash Received", "$15.00")
                .item("Change", "$2.62")
                .emptyLine()
                .footer("TXN001234", dateTime)
                .complete();
            
            System.out.println("Sample receipt printed!");
            
        } catch (Ncr7167Exception e) {
            System.err.println("Failed to print receipt: " + e.getMessage());
        }
    }
    
    private static void testBarCode(Ncr7167Printer printer) {
        try {
            System.out.println("Testing bar codes...");
            
            printer.selectReceiptStation();
            printer.printLine("=== BAR CODE TEST ===");
            printer.emptyLine();
            
            // Test different bar code types
            printer.printLine("Code 39:");
            printer.setBarCodeHeight(50);
            printer.printBarCode(Ncr7167Commands.BarCodeTypes.CODE39, "123456789");
            printer.emptyLine();
            
            printer.printLine("Code 128:");
            printer.printBarCode(Ncr7167Commands.BarCodeTypes.CODE128, "HELLO123");
            printer.emptyLine();
            
            printer.printLine("UPC-A:");
            printer.printBarCode(Ncr7167Commands.BarCodeTypes.UPC_A, "012345678905");
            printer.emptyLine();
            
            printer.feedPaper(2);
            System.out.println("Bar code test completed!");
            
        } catch (Ncr7167Exception e) {
            System.err.println("Bar code test failed: " + e.getMessage());
        }
    }
    
    private static void testFormatting(Ncr7167Printer printer) {
        try {
            System.out.println("Testing text formatting...");
            
            printer.selectReceiptStation();
            printer.printLine("=== FORMATTING TEST ===");
            printer.emptyLine();
            
            // Normal text
            printer.printLine("Normal text");
            
            // Bold/Emphasized
            printer.setEmphasized(true);
            printer.printLine("Bold/Emphasized text");
            printer.setEmphasized(false);
            
            // Underlined
            printer.setUnderline(true);
            printer.printLine("Underlined text");
            printer.setUnderline(false);
            
            // Double wide
            printer.setDoubleWide(true);
            printer.printLine("DOUBLE WIDE");
            printer.setDoubleWide(false);
            
            // Combined formatting
            printer.setEmphasized(true);
            printer.setUnderline(true);
            printer.printLine("Bold + Underline");
            printer.setEmphasized(false);
            printer.setUnderline(false);
            
            printer.feedPaper(3);
            System.out.println("Formatting test completed!");
            
        } catch (Ncr7167Exception e) {
            System.err.println("Formatting test failed: " + e.getMessage());
        }
    }
    
    private static void checkPrinterStatus(Ncr7167Printer printer) {
        try {
            System.out.println("Checking printer status...");
            
            int status = printer.requestStatus();
            System.out.println("Status byte: 0x" + Integer.toHexString(status));
            
            System.out.println("Paper present: " + printer.isPaperPresent());
            System.out.println("Paper low: " + printer.isPaperLow());
            
        } catch (Ncr7167Exception e) {
            System.err.println("Status check failed: " + e.getMessage());
        }
    }
    
    private static void testCashDrawer(Ncr7167Printer printer) {
        try {
            System.out.println("Testing cash drawer...");
            printer.openCashDrawer();
            System.out.println("Cash drawer opened!");
            
        } catch (Ncr7167Exception e) {
            System.err.println("Cash drawer test failed: " + e.getMessage());
        }
    }
    
    private static void testSlipStation(Ncr7167Printer printer) {
        try {
            System.out.println("Testing slip station...");
            
            printer.selectSlipStation();
            printer.printLine("=== SLIP STATION TEST ===");
            printer.printLine("This is printed on slip station");
            printer.printLine("Please insert form/check");
            printer.printLine("Test completed.");
            
            // Switch back to receipt station
            printer.selectReceiptStation();
            
            System.out.println("Slip station test completed!");
            
        } catch (Ncr7167Exception e) {
            System.err.println("Slip station test failed: " + e.getMessage());
        }
    }
}
