package simple.account.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import simple.account.demo.model.Transaction;
import simple.account.demo.service.TransferManager;

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
