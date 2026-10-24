package cli;

import application.IssueBookCommand;

import java.io.PrintStream;
import java.util.Scanner;

/** Pure Fabrication: лише зчитує текст із консолі й формує команду. Бізнес-логіки немає. */
public class ConsoleCommandParser {
    private final Scanner in;
    private final PrintStream out;
    private final boolean echoInput;

    public ConsoleCommandParser(Scanner in, PrintStream out, boolean echoInput) {
        this.in = in;
        this.out = out;
        this.echoInput = echoInput;
    }

    public String readMenuChoice() {
        out.print("Ваш вибір: ");
        return nextLine();
    }

    public IssueBookCommand readIssueBookCommand() {
        out.print("Введіть ім'я читача: ");
        String name = nextLine();
        out.print("Введіть категорію книги (Standard/Rare/Academic): ");
        String category = nextLine();
        return new IssueBookCommand(name, category);
    }

    private String nextLine() {
        String line = in.hasNextLine() ? in.nextLine().trim() : "0";
        if (echoInput) {
            out.println(line);
        }
        return line;
    }
}