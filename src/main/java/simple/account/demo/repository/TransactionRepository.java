package simple.account.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import simple.account.demo.model.Transaction;
import simple.account.demo.model.TransactionStatus;

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
