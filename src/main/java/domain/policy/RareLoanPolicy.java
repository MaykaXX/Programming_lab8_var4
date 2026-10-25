package domain.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Рідкісна книга: застава 50% вартості, збір 50 + 200 грн. */
public final class RareLoanPolicy implements LoanFeePolicy {
    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("0.50");
    private static final BigDecimal ADDITIONAL_FEE = new BigDecimal("200.00");

    @Override
    public LoanFee feeFor(BigDecimal bookValue) {
        return new LoanFee(
                bookValue.multiply(DEPOSIT_RATE).setScale(2, RoundingMode.HALF_UP),
                BASE_SERVICE_FEE.add(ADDITIONAL_FEE));
    }
}
