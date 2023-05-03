package omg.simple.account.core.controller;

import omg.simple.account.core.DbTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import omg.simple.account.core.model.Account;
import omg.simple.account.core.model.Transaction;
import omg.simple.account.core.model.TransactionStatus;
import omg.simple.account.core.repository.AccountRepository;
import omg.simple.account.core.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(MockitoExtension.class)
class TransactionControllerJourneyTest extends DbTestBase {
    @Autowired
    TransactionRepository tranRepo;
    @Autowired
    AccountRepository accRepo;

    Account ACC_THB = Account.builder().total(BigDecimal.valueOf(1000.0)).currency("THB").email("thb@bank.haha").build();
    Account ACC_EUR = Account.builder().total(BigDecimal.valueOf(10000.0)).currency("EUR").email("eur@bank.haha").build();

    @AfterEach
    void resetAccountTable(){
        accRepo.deleteAll();
        tranRepo.deleteAll();
    }

    @Test
    void transferToWithInApp(){
        Account accEUR = accRepo.save(ACC_EUR.toBuilder().build());
        long fromAccId = accEUR.getId();
        BigDecimal fromAccOldBalance = accEUR.getTotal();

        Account accTHB = accRepo.save(ACC_THB.toBuilder().build());
        long toAccId = accTHB.getId();
        BigDecimal toAccOldBalance = accTHB.getTotal();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(Transaction.builder().amount(BigDecimal.valueOf(500.0)).currency("USD").fromAcc(fromAccId).toAcc(toAccId).build())
                .when().post("/transfer-with-in-app").then()
                .statusCode(SC_OK);

        Account fromAccount = accRepo.findById(fromAccId).get();
        assertThat(fromAccount.getTotal()).isLessThan(fromAccOldBalance);
        Account toAccount = accRepo.findById(toAccId).get();
        assertThat(toAccount.getTotal()).isGreaterThan(toAccOldBalance);

        List<Transaction> transactions = tranRepo.findAll();
        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals(TransactionStatus.DONE, transaction.getTransaction_status());
    }

    @ParameterizedTest
    @MethodSource("badTransactionsDraft")
    void transferToWithInApp_valid_accountId_invalidTransaction(Transaction badTransaction){
        Account fromAcc = accRepo.save(ACC_EUR.toBuilder().build());
        long fromAccId = fromAcc.getId();
        BigDecimal fromAccOldBalance = fromAcc.getTotal();
        badTransaction.setFromAcc(fromAccId);

        Account toAcc = accRepo.save(ACC_THB.toBuilder().build());
        long toAccId = toAcc.getId();
        BigDecimal toAccOldBalance = toAcc.getTotal();
        badTransaction.setToAcc(toAccId);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(badTransaction)
                .when().post("/transfer-with-in-app").then()
                .statusCode(SC_BAD_REQUEST);

        checkTransactionFailNoAmountChanged(fromAcc, toAcc);
    }

    @Test
    void transferToWithInApp_same_accountID(){
        Account accEUR = accRepo.save(ACC_EUR.toBuilder().build());
        BigDecimal fromAccOldBalance = accEUR.getTotal();
        long fromAccId = accEUR.getId();
        long toAccId = accEUR.getId();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(Transaction.builder().amount(BigDecimal.valueOf(500.0)).currency("USD").fromAcc(fromAccId).toAcc(toAccId).build())
                .when().post("/transfer-with-in-app").then()
                .statusCode(SC_BAD_REQUEST);

        Account fromAccount = accRepo.findById(fromAccId).get();
        assertThat(fromAccount.getTotal()).isEqualByComparingTo(fromAccOldBalance);

        List<Transaction> transactions = tranRepo.findAll();
        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals(TransactionStatus.FAIL, transaction.getTransaction_status());
    }

    @Test
    void transferToWithInApp_unprocessable_account(){
        Account fromAcc = accRepo.save(ACC_EUR.toBuilder().build());
        long fromAccId = fromAcc.getId();

        Account toAcc = accRepo.save(ACC_THB.toBuilder().currency("huh??").build());
        long toAccId = toAcc.getId();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(Transaction.builder().amount(BigDecimal.valueOf(500.0)).currency("USD").fromAcc(fromAccId).toAcc(toAccId).build())
                .when().post("/transfer-with-in-app").then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        checkTransactionFailNoAmountChanged(fromAcc, toAcc);
    }

    void checkTransactionFailNoAmountChanged(Account fromAcc, Account toAcc){
        Account fromAccount = accRepo.findById(fromAcc.getId()).get();
        Account toAccount = accRepo.findById(toAcc.getId()).get();
        assertThat(fromAccount.getTotal()).isEqualByComparingTo(fromAcc.getTotal());
        assertThat(toAccount.getTotal()).isEqualByComparingTo(toAcc.getTotal());

        List<Transaction> transactions = tranRepo.findAll();
        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals(TransactionStatus.FAIL, transaction.getTransaction_status());
    }

    static Stream<Transaction> badTransactionsDraft(){
        Transaction transaction = new Transaction(null,"USD", 0L, 0L, TEN, null, null, null);
        return Stream.of(
                transaction.toBuilder().currency(null).build(),
                transaction.toBuilder().currency("  TH B").build(),
                transaction.toBuilder().currency("").build(),
                transaction.toBuilder().currency("    ").build(),
                transaction.toBuilder().amount(null).build(),
                transaction.toBuilder().amount(ZERO).build(),
                transaction.toBuilder().amount(TEN.negate()).build()
        );
    }
}