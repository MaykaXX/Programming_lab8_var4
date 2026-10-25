package application;

import domain.LoanReceipt;

import java.util.List;

/**
 * Use-Case Controller (GRASP Controller): перший об'єкт після UI.
 * Приймає команду, делегує сценарій, сповіщає підписників.
 * Не містить ні консольного вводу/виводу, ні обчислень.
 */
public class IssueBookController {
    private final LoanIssuer issuer;
    private final List<LoanIssuedListener> listeners;

    public IssueBookController(LoanIssuer issuer, List<LoanIssuedListener> listeners) {
        this.issuer = issuer;
        this.listeners = List.copyOf(listeners);
    }

    public LoanReceipt issueBook(IssueBookCommand command) {
        LoanReceipt receipt = issuer.issue(command);
        for (LoanIssuedListener listener : listeners) {
            listener.onLoanIssued(receipt);
        }
        return receipt;
    }
}
