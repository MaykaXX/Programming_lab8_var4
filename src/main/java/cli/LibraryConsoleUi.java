package cli;

import application.IssueBookController;
import domain.LoanReceipt;

import java.io.PrintStream;

/** Thin Console UI: меню, виклик контролера, виведення результату. Жодної бізнес-логіки. */
public class LibraryConsoleUi {
    private final ConsoleCommandParser parser;
    private final IssueBookController controller;
    private final PrintStream out;

    public LibraryConsoleUi(ConsoleCommandParser parser, IssueBookController controller, PrintStream out) {
        this.parser = parser;
        this.controller = controller;
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
            out.println(format(receipt));
        } catch (IllegalArgumentException | IllegalStateException e) {
            out.println("Помилка: " + e.getMessage());
        }
    }

    private String format(LoanReceipt receipt) {
        return "Квитанція видачі #" + receipt.loanId()
                + "\nЧитач: " + receipt.readerName() + " (" + receipt.ticketNumber() + ")"
                + "\nКнига: " + receipt.bookTitle()
                + "\nЗастава: " + receipt.depositFee() + " грн"
                + "\nСервісний збір: " + receipt.serviceFee() + " грн"
                + "\nРазом: " + receipt.totalPayable() + " грн"
                + "\nДата повернення: " + receipt.dueDate();
    }
}
