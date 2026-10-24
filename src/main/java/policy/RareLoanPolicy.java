package policy;

public class RareLoanPolicy implements LoanFeePolicy {
    private static final double DEPOSIT_RATE = 0.50; // це є 50% від вартості книги
    private static final double BASE_SERVICE_FEE = 50.0;
    private static final double ADDITIONAL_FEE = 200.0;

    @Override
    public double calculateDeposit(double bookValue) {
        return bookValue * DEPOSIT_RATE;
    }

    @Override
    public double calculateServiceFee() {
        return BASE_SERVICE_FEE + ADDITIONAL_FEE; // 250 грн
    }
}