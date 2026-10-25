package domain.policy;

import java.math.BigDecimal;

/**
 * Стабільний контракт розрахунку вартості видачі (Protected Variations).
 * Один метод: застава й збір рахуються разом, тому кожна реалізація має LCOM4 = 1.
 */
public interface LoanFeePolicy {
    BigDecimal BASE_SERVICE_FEE = new BigDecimal("50.00");

    LoanFee feeFor(BigDecimal bookValue);
}
