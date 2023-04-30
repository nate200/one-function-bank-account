package simple.account.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder=true)

@Entity
@Table(name = "TRANSACTION")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    Long transactionId;

    @NotNull String currency;
    @Column(name = "from_acc")
    @NotNull long fromAcc;
    @Column(name = "to_acc")
    @NotNull long toAcc;
    @NotNull BigDecimal amount;
    @NotNull @Enumerated(EnumType.STRING)
    @NotNull TransactionStatus transaction_status;
    @NotNull String transaction_result;
}
/*
CREATE TABLE TRANSACTION (
    transaction_id serial PRIMARY KEY,
    currency VARCHAR ( 10 ) NOT NULL,
    from_acc integer references ACCOUNT (id) NOT NULL,
    to_acc integer references ACCOUNT (id) NOT NULL,
    amount DECIMAL NOT NULL.
    transaction_status VARCHAR ( 20 ) NOT NULL,
    transaction_result VARCHAR ( 30 ) NOT NULL
);

CREATE TABLE TRANSACTION (
    transaction_id serial PRIMARY KEY,
    currency VARCHAR ( 10 ) NOT NULL,
    from_acc integer NOT NULL,
    to_acc integer NOT NULL,
    amount DECIMAL NOT NULL,
    transaction_status VARCHAR ( 20 ) NOT NULL,
    transaction_result VARCHAR ( 30 ) NOT NULL
);
insert into transaction (amount, currency, from_acc, to_acc, transaction_result, transaction_status) values (1, 'THB', 1, 2, 'PROCESSING', 'plz wait ;)');
UPDATE Transaction
    SET transaction_status = 'DONE' , transaction_result = 'done and done'
    WHERE transaction_id = 1;
*/