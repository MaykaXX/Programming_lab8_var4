package policy;

import java.math.BigDecimal;

public interface LoanFeePolicy {
    BigDecimal calculateDeposit(BigDecimal bookValue);
    BigDecimal calculateServiceFee();
}