package omg.simple.account.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import omg.simple.account.core.model.business.Transaction;
import omg.simple.account.core.model.constant.TransactionStatus;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Modifying
    @Query("""
        UPDATE Transaction
        SET transaction_status = :status , transaction_result = :result
        WHERE transactionId = :transactionId
    """)
    int updateStatus(
        @Param("status") TransactionStatus status,
        @Param("result") String result,
        @Param("transactionId") long transactionId
    );
}
