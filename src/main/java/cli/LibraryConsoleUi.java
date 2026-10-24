package cli;

import application.IssueBookController;
import domain.LoanReceipt;

import java.io.PrintStream;

/** Thin Console UI: меню, виклик контролера, виведення результату. Жодної бізнес-логіки. */
public class LibraryConsoleUi {
    private final ConsoleCommandParser parser;
    private final IssueBookController controller;
    private final ReceiptFormatter formatter;
    private final PrintStream out;

    public LibraryConsoleUi(ConsoleCommandParser parser, IssueBookController controller,
                            ReceiptFormatter formatter, PrintStream out) {
        this.parser = parser;
        this.controller = controller;
        this.formatter = formatter;
        this.out = out;
    }

    public void run() {
        while (true) {
            out.println("\n=== Бібліотека ===\n1 — Видати книгу\n0 — Вихід");
            String choice = parser.readMenuChoice();
            if ("0".equals(choice)) {
                out.println("До побачення!");
                return;
            }
            if ("1".equals(choice)) {
                issueBook();
            } else {
                out.println("Невідома команда: " + choice);
            }
        }
    }

    private void issueBook() {
        try {
            LoanReceipt receipt = controller.issueBook(parser.readIssueBookCommand());
            out.println(formatter.format(receipt));
        } catch (IllegalArgumentException | IllegalStateException e) {
            out.println("Помилка: " + e.getMessage());
        }
    }
}
