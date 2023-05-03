package omg.simple.account.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import omg.simple.account.core.model.Transaction;
import omg.simple.account.core.service.TransferManager;

@RestController
@AllArgsConstructor
public class TransactionController {
    TransferManager transferManager;

    @PostMapping("transfer-with-in-app")
    public ResponseEntity<String> transferToWithInApp(@RequestBody Transaction transaction) throws Exception{
        transferManager.transferWithInApp(transaction);
        return ResponseEntity.ok("Transfer successful");
    }
}
