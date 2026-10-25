package domain.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Навчальна книга: застава 20% вартості, збір 50 + 50 грн. */
public final class AcademicLoanPolicy implements LoanFeePolicy {
    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("0.20");
    private static final BigDecimal ADDITIONAL_FEE = new BigDecimal("50.00");

    @Override
    public LoanFee feeFor(BigDecimal bookValue) {
        return new LoanFee(
                bookValue.multiply(DEPOSIT_RATE).setScale(2, RoundingMode.HALF_UP),
                BASE_SERVICE_FEE.add(ADDITIONAL_FEE));
    }
}
