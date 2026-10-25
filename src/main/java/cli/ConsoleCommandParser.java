package cli;

import application.IssueBookCommand;
import domain.BookCategory;

import java.io.PrintStream;
import java.util.Scanner;

/** Pure Fabrication: зчитує текстові команди зі Scanner і формує команду. Бізнес-логіки немає. */
public class ConsoleCommandParser {
    private final Scanner in;
    private final PrintStream out;

    public ConsoleCommandParser(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    /** Кінець введення трактується як вихід із програми. */
    public String readMenuChoice() {
        out.print("Ваш вибір: ");
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    public IssueBookCommand readIssueBookCommand() {
        String name = ask("Введіть ім'я читача: ");
        String category = ask("Введіть категорію книги (Standard/Rare/Academic): ");
        return new IssueBookCommand(name, BookCategory.parse(category));
    }

    private String ask(String prompt) {
        out.print(prompt);
        if (!in.hasNextLine()) {
            throw new IllegalStateException("Введення завершено, команду не отримано.");
        }
        return in.nextLine().trim();
    }
}
