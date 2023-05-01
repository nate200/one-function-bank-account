package simple.account.demo.controller;

import io.restassured.RestAssured;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import simple.account.demo.DbTestBase;
import simple.account.demo.RecursiveCompareWithBigDecimal;
import simple.account.demo.controller.ControllerExceptionHandler;
import simple.account.demo.model.Account;
import simple.account.demo.repository.AccountRepository;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static java.math.BigDecimal.TEN;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)//https://stackoverflow.com/questions/32054274/connection-refused-with-rest-assured-junit-test-case
public class AccountControllerJourneyTest extends DbTestBase {

    @Autowired
    AccountRepository accRepo;

    @BeforeEach
    void resetAccountTable(){
        accRepo.deleteAll();
    }

    @Test
    void createAccount(){
        assertEquals(0,accRepo.count());

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(Account.builder().total(TEN).currency("THB").email("a@a.com").build())
            .when().post("/create-account").then()
            .statusCode(SC_OK);

        assertEquals(1,accRepo.count());
    }
    @ParameterizedTest
    @MethodSource("simple.account.demo.TestDataProvider#badNewAccountRequest")
    void createAccount_invalidAccount(Account invalidAcc){
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(invalidAcc)
                .when().post("/create-account").then()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    void getAccount(){
        Account expected = accRepo.save(Account.builder().total(TEN).currency("THB").email("a@a.com").build());

        Account actual = given().when()
                .pathParam("accId", expected.getId())
                .get("/getAccount/{accId}").as(Account.class);

        RecursiveCompareWithBigDecimal.compareNoNull(expected, actual);
    }
    @Test
    void getAccount_404(){
        given().when().get("/getAccount/-1").then().statusCode(404);
    }
}
