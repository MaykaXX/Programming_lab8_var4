package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Читач (Rich Domain Model, корінь агрегату): сам перевіряє ліміт і сам створює видачу (Creator).
 * Кількість активних видач не зберігається окремим полем, а виводиться зі стану —
 * одне джерело істини.
 */
public final class Reader {
    public static final int MAX_ACTIVE_LOANS = 5;

    private final int id;
    private final String name;
    private final String ticketNumber;
    private final String email;
    private final int loansHeldBefore;
    private final List<Loan> loans = new ArrayList<>();

    private Reader(int id, String name, String ticketNumber, String email, int loansHeldBefore) {
        this.id = id;
        this.name = name;
        this.ticketNumber = ticketNumber;
        this.email = email;
        this.loansHeldBefore = loansHeldBefore;
    }

    /** Static Factory Method (Creator); loansHeldBefore - видачі, оформлені до цього сеансу. */
    public static Reader register(int id, String name, String ticketNumber, String email, int loansHeldBefore) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ім'я читача не може бути порожнім.");
        }
        if (ticketNumber == null || ticketNumber.isBlank()) {
            throw new IllegalArgumentException("Номер квитка обов'язковий.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Некоректний email читача.");
        }
        if (loansHeldBefore < 0) {
            throw new IllegalArgumentException("Кількість активних видач не може бути від'ємною.");
        }
        return new Reader(id, name, ticketNumber, email, loansHeldBefore);
    }

    public int activeLoansCount() {
        return loansHeldBefore + loans.size();
    }

    public boolean canBorrow() {
        return activeLoansCount() < MAX_ACTIVE_LOANS;
    }

    /** Creator: читач «записує» свої видачі, тому саме він їх створює. Повертає незмінну квитанцію. */
    public LoanReceipt borrow(int loanId, Book book, int days, LocalDate issuedOn) {
        if (!canBorrow()) {
            throw new IllegalStateException("Читач " + name + " вже має " + activeLoansCount()
                    + " активних видач (ліміт " + MAX_ACTIVE_LOANS + ").");
        }
        Loan loan = Loan.issue(loanId, this, book, days, issuedOn);
        loans.add(loan);
        return loan.toReceipt();
    }

    /** Незмінна копія: зовні колекцію змінити не можна. */
    public List<Loan> getLoans() {
        return List.copyOf(loans);
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
