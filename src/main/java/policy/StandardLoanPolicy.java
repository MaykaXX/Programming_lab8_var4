package policy;

import java.math.BigDecimal;

public class StandardLoanPolicy implements LoanFeePolicy {
    private static final BigDecimal BASE_SERVICE_FEE = new BigDecimal("50.00");

    @Override
    public BigDecimal calculateDeposit(BigDecimal bookValue) {
        return new BigDecimal("0.00");
    }

    @Override
    public BigDecimal calculateServiceFee() {
        return BASE_SERVICE_FEE;
    }
}