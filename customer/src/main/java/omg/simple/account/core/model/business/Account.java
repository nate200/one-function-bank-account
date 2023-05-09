package omg.simple.account.core.model.business;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(0)
    @NotNull
    private BigDecimal total;

    @NotNull
    private String currency;

    @NotNull
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9+_.-]+\\.(.+)$")//simple, for now
    private String email;
}

/*CREATE TABLE ACCOUNT (
    id serial PRIMARY KEY,
    total DECIMAL NOT NULL CHECK ( total >= 0),
    currency VARCHAR ( 10 ) NOT NULL,
    email VARCHAR ( 50 ) NOT NULL
);

INSERT INTO ACCOUNT(id, total, currency, email) VALUES(1,50.0,'THB', "a@a.com");
INSERT INTO ACCOUNT(id, total, currency, email) VALUES(2,50000.0,'USD', "a@a.com");
*/
