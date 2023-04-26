package simple.account.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TRANSACTION")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString @Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    Long transactionId;

    @NotNull String currency;
    @Column(name = "from_acc")
    @NotNull Long fromAcc;
    @Column(name = "to_acc")
    @NotNull Long toAcc;
    @NotNull BigDecimal amount;
    @NotNull String transaction_result;
}
/*
CREATE TABLE TRANSACTION (
    transaction_id serial PRIMARY KEY,
    currency VARCHAR ( 10 ) NOT NULL,
    from_acc integer references ACCOUNT (id) NOT NULL,
    to_acc integer references ACCOUNT (id) NOT NULL,
    amount DECIMAL NOT NULL
);

CREATE TABLE TRANSACTION (
    transaction_id serial PRIMARY KEY,
    currency VARCHAR ( 10 ) NOT NULL,
    from_acc integer NOT NULL,
    to_acc integer NOT NULL,
    amount DECIMAL NOT NULL
);
*/