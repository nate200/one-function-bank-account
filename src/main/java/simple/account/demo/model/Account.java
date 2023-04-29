package simple.account.demo.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Min(0)
    @NotNull
    BigDecimal total;

    @NotNull
    String currency;
}

/*CREATE TABLE ACCOUNT (
    id serial PRIMARY KEY,
    total DECIMAL NOT NULL CHECK ( total >= 0),
    currency VARCHAR ( 10 ) NOT NULL
);

INSERT INTO ACCOUNT(id, total, currency) VALUES(1,50.0,'THB');
INSERT INTO ACCOUNT(id, total, currency) VALUES(2,50000.0,'USD');
*/
