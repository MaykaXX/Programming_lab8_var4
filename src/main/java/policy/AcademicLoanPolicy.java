package policy;

public class AcademicLoanPolicy implements LoanFeePolicy {
    private static final double DEPOSIT_RATE = 0.20;   // це є 20% від вартості книги
    private static final double BASE_SERVICE_FEE = 50.0;
    private static final double ADDITIONAL_FEE = 50.0;

    @Override
    public double calculateDeposit(double bookValue) {
        return bookValue * DEPOSIT_RATE;
    }

    @Override
    public double calculateServiceFee() {
        return BASE_SERVICE_FEE + ADDITIONAL_FEE; // 100 грн
    }
}