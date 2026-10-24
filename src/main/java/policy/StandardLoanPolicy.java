package policy;

public class StandardLoanPolicy implements LoanFeePolicy {
    private static final double BASE_SERVICE_FEE = 50.0;

    @Override
    public double calculateDeposit(double bookValue) {
        return 0.0;
    }

    @Override
    public double calculateServiceFee() {
        return BASE_SERVICE_FEE;
    }
}