package app;

import application.IssueBookController;
import application.LoanIssuer;
import cli.ConsoleCommandParser;
import cli.LibraryConsoleUi;
import infrastructure.EmailNotificationService;
import infrastructure.FileAuditLogger;
import infrastructure.InMemoryBookCatalog;
import infrastructure.InMemoryReaderRepository;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.Scanner;

/** Composition Root: єдине місце, де збираються всі частини системи, і точка входу. */
public class Main {
    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Clock clock = Clock.systemDefaultZone();

        LoanIssuer issuer = new LoanIssuer(new InMemoryReaderRepository(), new InMemoryBookCatalog(), clock);
        IssueBookController controller = new IssueBookController(issuer, List.of(
                new FileAuditLogger(Path.of("library_audit.log"), clock),
                new EmailNotificationService(out)));

        new LibraryConsoleUi(
                new ConsoleCommandParser(new Scanner(System.in, StandardCharsets.UTF_8), out),
                controller,
                out).run();
    }
}
