package omg.simple.account.core.model.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import omg.simple.account.core.model.constant.TransactionStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder=true)

@Entity
@Table(name = "TRANSACTION")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    Long transactionId;

    String currency;
    @Column(name = "from_acc")
    long fromAcc;
    @Column(name = "to_acc")
    long toAcc;
    BigDecimal amount;

    @JsonIgnore//hide from swagger schema
    @Enumerated(EnumType.STRING)
    @NotNull TransactionStatus transaction_status;

    @JsonIgnore
    @NotNull String transaction_result;

    @JsonIgnore
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    Date createTime;
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