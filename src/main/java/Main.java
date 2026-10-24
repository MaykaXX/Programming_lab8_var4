import application.IssueBookController;
import application.LoanIssuedListener;
import cli.ConsoleCommandParser;
import cli.LibraryConsoleUi;
import cli.ReceiptFormatter;
import domain.Book;
import domain.BookCategory;
import domain.Reader;
import infrastructure.InMemoryBookCatalog;
import infrastructure.InMemoryReaderRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Сценарій, який «вводить» користувач у консолі
    private static final String SCRIPTED_SESSION = String.join("\n",
            "1", "Іван Петренко", "Rare",
            "1", "Іван Петренко", "Academic",
            "1", "Іван Петренко", "Standard",
            "1", "Марія Мельник", "Standard",   // ліміт вичерпано
            "1", "Іван Петренко", "Comics",     // невідома категорія
            "1", "Невідомий Читач", "Standard", // читача немає
            "0");

    public static void main(String[] args) {
        if (args.length > 0 && "interactive".equals(args[0])) {
            runUi(new Scanner(System.in), false);
            return;
        }
        demoTask2();
        demoTask3();
    }

    // ---------- Завдання 2: Information Expert + Creator ----------
    private static void demoTask2() {
        System.out.println("-------- ТЕСТ ЗАВДАННЯ 2 (Information Expert + Creator) --------");

        // Information Expert: книга сама рахує заставу й збір
        Book rare = Book.of(102, "Кобзар (прижиттєве видання)", BookCategory.RARE, new BigDecimal("1500.00"));
        System.out.println("Книга сама рахує: застава = " + rare.depositFee() + ", збір = " + rare.serviceFee());

        // Creator: читач сам створює видачу
        Reader reader = Reader.register(9, "Олег Бондар", "T-9377", "oleg@univ.edu", 0);
        reader.borrow(rare, 14, LocalDate.now());
        System.out.println("Читач сам створив видачу, активних: " + reader.getActiveLoansCount());

        // Інкапсуляція: колекцію ззовні змінити не можна
        try {
            reader.getLoans().add(null);
        } catch (UnsupportedOperationException e) {
            System.out.println("Колекцію видач ззовні змінити не можна (List.copyOf).");
        }

        // Створення в невалідному стані неможливе
        try {
            Book.of(1, " ", BookCategory.STANDARD, BigDecimal.TEN);
        } catch (IllegalArgumentException e) {
            System.out.println("Невалідну книгу створити неможливо: " + e.getMessage());
        }
        try {
            Reader.register(1, "Тест", "T-1", "без-пошти", 0);
        } catch (IllegalArgumentException e) {
            System.out.println("Невалідного читача створити неможливо: " + e.getMessage());
        }
    }

    // ---------- Завдання 3: CLI -> Controller -> Domain ----------
    private static void demoTask3() {
        System.out.println("\n-------- ТЕСТ ЗАВДАННЯ 3 (Controller + Indirection + Pure Fabrication) --------");
        runUi(new Scanner(SCRIPTED_SESSION), true);
    }

    private static void runUi(Scanner scanner, boolean echoInput) {
        // Сповіщення підключаються через інтерфейс LoanIssuedListener (Indirection)
        LoanIssuedListener auditLog = receipt -> System.out.println("[Журнал логів] Видачу книги #"
                + receipt.bookId() + " читачу #" + receipt.readerId() + " записано в library_audit.log.");
        LoanIssuedListener emailReminder = receipt -> System.out.println("[Email] Нагадування надіслано на "
                + receipt.readerEmail() + " (повернути до " + receipt.dueDate() + ")");

        // Composition Root: тут збираємо всі частини
        IssueBookController controller = new IssueBookController(
                new InMemoryReaderRepository(),
                new InMemoryBookCatalog(),
                List.of(auditLog, emailReminder),
                Clock.systemDefaultZone());

        new LibraryConsoleUi(
                new ConsoleCommandParser(scanner, System.out, echoInput),
                controller,
                new ReceiptFormatter(),
                System.out).run();
    }
}
