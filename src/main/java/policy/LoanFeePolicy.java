package policy;

public interface LoanFeePolicy {
    double calculateDeposit(double bookValue);
    double calculateServiceFee();
}