package domain.policy;

import java.math.BigDecimal;

/** Звичайна книга: без застави, лише базовий збір. */
public final class StandardLoanPolicy implements LoanFeePolicy {
    @Override
    public LoanFee feeFor(BigDecimal bookValue) {
        return new LoanFee(new BigDecimal("0.00"), BASE_SERVICE_FEE);
    }
}
