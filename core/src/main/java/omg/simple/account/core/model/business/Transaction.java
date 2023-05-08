package omg.simple.account.core.model.business;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    private String currency;
    @Column(name = "from_acc")
    private long fromAcc;
    @Column(name = "to_acc")
    private long toAcc;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionStatus transaction_status;

    @NotNull
    private String transaction_result;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;
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