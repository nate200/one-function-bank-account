package simple.account.demo.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    BigDecimal total;
    @Nonnull
    String currency;
}

/*CREATE TABLE ACCOUNT (
    id serial PRIMARY KEY,
    total DECIMAL NOT NULL CHECK ( total >= 0),
    currency VARCHAR ( 10 ) NOT NULL
);

INSERT INTO ACCOUNT VALUES(1,50.0,'THB');
INSERT INTO ACCOUNT VALUES(2,50000.0,'USD');
*/
