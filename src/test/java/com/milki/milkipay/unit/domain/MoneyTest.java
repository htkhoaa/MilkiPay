package com.milki.milkipay.unit.domain;

import com.milki.milkipay.domain.model.vo.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

class MoneyTest {

    private static final Currency VND = Currency.getInstance("VND");
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void shouldCreateMoney() {
        Money money = new Money(100_000, VND);

        assertThat(money.amount()).isEqualTo(100_000);
        assertThat(money.currency()).isEqualTo(VND);
    }

    @Test
    void shouldRejectNonPositiveAmount() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Money(0, VND));

        assertThatIllegalArgumentException().isThrownBy(() -> new Money(-1, VND));
    }

    @Test
    void shouldConvertBetweenMajorAndMinorUnits() {
        Money money = new Money(new BigDecimal("100000"), VND);

        assertThat(money.amount()).isEqualTo(100_000);
        assertThat(money.toMajorUnits()).isEqualByComparingTo("100000");
    }

    @Test
    void shouldAddMoney() {
        Money first = new Money(100_000, VND);
        Money second = new Money(50_000, VND);

        Money result = first.add(second);

        assertThat(result).isEqualTo(new Money(150_000, VND));
        assertThat(first.amount()).isEqualTo(100_000);
    }

    @Test
    void shouldSubtractMoney() {
        Money first = new Money(100_000, VND);
        Money second = new Money(50_000, VND);

        Money result = first.subtract(second);

        assertThat(result).isEqualTo(new Money(50_000, VND));
        assertThat(first.amount()).isEqualTo(100_000);
    }

    @Test
    void shouldRejectNonPositiveSubtractionResult() {
        Money money = new Money(100_000, VND);

        assertThatIllegalArgumentException().isThrownBy(() -> money.subtract(new Money(100_000, VND)));

        assertThatIllegalArgumentException().isThrownBy(() -> money.subtract(new Money(150_000, VND)));
    }

    @Test
    void shouldRejectDifferentCurrencies() {
        Money vnd = new Money(100_000, VND);
        Money usd = new Money(100, USD);

        assertThatIllegalArgumentException().isThrownBy(() -> vnd.add(usd));

        assertThatIllegalArgumentException().isThrownBy(() -> vnd.subtract(usd));
    }

    @Test
    void shouldRejectAdditionOverflow() {
        Money money = new Money(Long.MAX_VALUE, VND);

        assertThatThrownBy(() -> money.add(new Money(1, VND)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void shouldHaveValueEquality() {
        Money first = new Money(100_000, VND);
        Money second = new Money(100_000, VND);

        assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);

        assertThat(first).isNotEqualTo(new Money(200_000, VND));
    }
}
