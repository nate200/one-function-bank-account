package omg.simple.account.core.controller;

import omg.simple.account.core.DbTestBase;
import omg.simple.account.core.RecursiveCompareWithBigDecimal;
import omg.simple.account.core.config.CoreSecurityComponentConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import omg.simple.account.core.model.business.Account;
import omg.simple.account.core.repository.AccountRepository;

import static io.restassured.RestAssured.given;
import static java.math.BigDecimal.TEN;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)//https://stackoverflow.com/questions/32054274/connection-refused-with-rest-assured-junit-test-case
public class AccountControllerJourneyTest extends DbTestBase {

    @Value("${god-token}")
    String token;

    @Autowired
    AccountRepository accRepo;

    @BeforeEach
    void resetAccountTable(){
        accRepo.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("omg.simple.account.core.TestDataProvider#validAccounts")
    void createAccount(Account validAccount){
        assertEquals(0,accRepo.count());

        given()
            .header("Authorization", "Bearer " + token)//.auth().oauth2(token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(validAccount)
            .when().post("/create-account").then()
            .statusCode(SC_OK);

        assertEquals(1,accRepo.count());
    }
    @ParameterizedTest
    @MethodSource("omg.simple.account.core.TestDataProvider#badNewAccountRequest")
    void createAccount_invalidAccount(Account invalidAcc){
        given().header("Authorization", "Bearer " + token)//.auth().oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(invalidAcc)
                .when().post("/create-account").then()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    void getAccount(){
        Account expected = accRepo.save(Account.builder().total(TEN).currency("THB").email("admin@admin.com").build());

        Account actual = given().when().header("Authorization", "Bearer " + token)//.auth().oauth2(token)
                .pathParam("accId", expected.getId())
                .get("/getAccount/{accId}").then()
                .statusCode(200)
                .extract().as(Account.class);

        RecursiveCompareWithBigDecimal.compareNoNull(expected, actual);
    }
    @Test
    void getAccount_404(){
        given().when().header("Authorization", "Bearer " + token)//.auth().oauth2(token)
                .get("/getAccount/-1").then().statusCode(404);
    }
}
