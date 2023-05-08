package omg.simple.account.core;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class RecursiveCompareWithBigDecimal {
    public static void compareNoNull(Object expected, Object actual){
        assertThat(actual)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .hasNoNullFieldsOrProperties()//assertNotNull(actual.getCreateTime());//need @EnableJpaAuditing and @EntityListeners(AuditingEntityListener.class)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
