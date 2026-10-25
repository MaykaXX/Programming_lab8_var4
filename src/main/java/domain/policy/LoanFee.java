package domain.policy;

import java.math.BigDecimal;

/** Незмінний результат розрахунку: застава + сервісний збір. */
public record LoanFee(BigDecimal deposit, BigDecimal serviceFee) {

    public LoanFee {
        if (deposit == null || serviceFee == null || deposit.signum() < 0 || serviceFee.signum() < 0) {
            throw new IllegalArgumentException("Застава й збір мають бути невід'ємними.");
        }
    }

    public BigDecimal total() {
        return deposit.add(serviceFee);
    }
}
