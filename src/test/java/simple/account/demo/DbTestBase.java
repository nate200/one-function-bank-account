package simple.account.demo;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestClass;

//@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DbTestBase {
    static final Gson GSON = new Gson();
}
