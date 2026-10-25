package domain.policy;

/**
 * Реєстр стратегій (Dictionary Dispatch): єдине місце, де створюються політики.
 * Додати нову політику = додати клас і один рядок тут.
 */
public final class LoanPolicies {
    public static final LoanFeePolicy STANDARD = new StandardLoanPolicy();
    public static final LoanFeePolicy ACADEMIC = new AcademicLoanPolicy();
    public static final LoanFeePolicy RARE = new RareLoanPolicy();

    private LoanPolicies() {
    }
}
