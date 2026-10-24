package cli;

import domain.LoanReceipt;

/* Pure Fabrication: форматування квитанції. */
public class ReceiptFormatter {
    public String format(LoanReceipt receipt) {
        return "Квитанція видачі #" + receipt.loanId()
                + "\nЧитач: " + receipt.readerName() + " (" + receipt.ticketNumber() + ")"
                + "\nКнига: " + receipt.bookTitle()
                + "\nЗастава: " + receipt.depositFee() + " грн"
                + "\nСервісний збір: " + receipt.serviceFee() + " грн"
                + "\nРазом: " + receipt.totalPayable() + " грн"
                + "\nДата повернення: " + receipt.dueDate();
    }
}
