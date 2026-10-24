package policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RareLoanPolicy implements LoanFeePolicy {
    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("0.50"); // 50% від вартості
    private static final BigDecimal BASE_SERVICE_FEE = new BigDecimal("50.00");
    private static final BigDecimal ADDITIONAL_FEE = new BigDecimal("200.00");

    @Override
    public BigDecimal calculateDeposit(BigDecimal bookValue) {
        return bookValue.multiply(DEPOSIT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateServiceFee() {
        return BASE_SERVICE_FEE.add(ADDITIONAL_FEE); // 250.00 грн
    }
}