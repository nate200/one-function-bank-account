package simple.account.demo.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TRANSACTION")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    Long transactionId;

    @Nonnull String currency;
    @Column(name = "from_acc")
    @Nonnull Long fromAcc;
    @Column(name = "to_acc")
    @Nonnull Long toAcc;
    @Nonnull BigDecimal amount;
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