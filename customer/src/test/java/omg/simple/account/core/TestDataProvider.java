package omg.simple.account.core;

import omg.simple.account.core.model.business.Account;

import java.util.stream.Stream;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;

public class TestDataProvider {
    static Stream<Account> validAccounts(){
        return Stream.of(
                Account.builder().total(ZERO).currency("THB").email("a@a.com").build(),
                Account.builder().total(TEN).currency("THB").email("a@a.com").build(),
                Account.builder().total(TEN).currency("     THB").email("a@a.com").build(),
                Account.builder().total(TEN).currency("THB       ").email("a@a.com").build(),
                Account.builder().total(TEN).currency("     THB     ").email("a@a.com").build(),
                Account.builder().total(TEN).currency("THB").email("     a@a.com").build(),
                Account.builder().total(TEN).currency("THB").email("a@a.com     ").build(),
                Account.builder().total(TEN).currency("THB").email("      a@a.com     ").build()
        );
    }
    static Stream<Account> badNewAccountRequest(){
        String validCurrency = "THB";
        String validEmail = "a@a.com";
        return Stream.of(
                Account.builder().total(null).currency(validCurrency).email(validEmail).build(),
                Account.builder().total(TEN).currency(null).email(validEmail).build(),
                Account.builder().total(TEN).currency(" TH B").email(validEmail).build(),
                Account.builder().total(TEN).currency(" THICCCCCC ").email(validEmail).build(),
                Account.builder().total(TEN).currency(validCurrency).email(null).build(),
                Account.builder().total(TEN).currency(validCurrency).email("").build(),
                Account.builder().total(TEN).currency(validCurrency).email("   ").build(),
                Account.builder().total(TEN).currency(validCurrency).email(" aaa   ").build(),
                Account.builder().total(TEN).currency(validCurrency).email(" aaa @a.com   ").build(),
                Account.builder().total(TEN).currency(validCurrency).email(" a aa@a.com   ").build()
        );
    }
}
