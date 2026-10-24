package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Читач (Rich Domain Model): сам перевіряє ліміт і сам створює видачу (Creator). */
public final class Reader {
    public static final int MAX_ACTIVE_LOANS = 5;

    private final int id;
    private final String name;
    private final String ticketNumber;
    private final String email;
    private int activeLoansCount;
    private final List<Loan> loans = new ArrayList<>();

    private Reader(int id, String name, String ticketNumber, String email, int activeLoansCount) {
        this.id = id;
        this.name = name;
        this.ticketNumber = ticketNumber;
        this.email = email;
        this.activeLoansCount = activeLoansCount;
    }

    public static Reader register(int id, String name, String ticketNumber, String email, int activeLoansCount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ім'я читача не може бути порожнім.");
        }
        if (ticketNumber == null || ticketNumber.isBlank()) {
            throw new IllegalArgumentException("Номер квитка обов'язковий.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Некоректний email читача.");
        }
        if (activeLoansCount < 0) {
            throw new IllegalArgumentException("Кількість активних видач не може бути від'ємною.");
        }
        return new Reader(id, name, ticketNumber, email, activeLoansCount);
    }

    public boolean canBorrow() {
        return activeLoansCount < MAX_ACTIVE_LOANS;
    }

    /** Creator: читач «записує» свої видачі, тому саме він їх створює. */
    public Loan borrow(Book book, int days, LocalDate issuedOn) {
        if (!canBorrow()) {
            throw new IllegalStateException("Читач " + name + " вже має " + activeLoansCount
                    + " активних видач (ліміт " + MAX_ACTIVE_LOANS + ").");
        }
        Loan loan = Loan.issue(this, book, days, issuedOn);
        loans.add(loan);
        activeLoansCount++;
        return loan;
    }

    /** Незмінна копія: зовні колекцію змінити не можна. */
    public List<Loan> getLoans() {
        return List.copyOf(loans);
    }

    public int getActiveLoansCount() {
        return activeLoansCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getEmail() {
        return email;
    }
}