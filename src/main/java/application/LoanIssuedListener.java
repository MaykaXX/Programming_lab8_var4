package application;

import domain.LoanReceipt;

/** Точка варіацій (Protected Variations): журнал, email, SMS... додаються без зміни контролера. */
public interface LoanIssuedListener {
    void onLoanIssued(LoanReceipt receipt);
}
