package omg.simple.account.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import omg.simple.account.core.model.Account;
import omg.simple.account.core.service.AccountService;

@RestController
@AllArgsConstructor
public class AccountController {
    AccountService accountService;

    @PostMapping("create-account")
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        accountService.createAccountRequest(account);
        return ResponseEntity.ok("saved");
    }

    @GetMapping("getAccount/{accId}")
    public Account getAccount(@PathVariable("accId") long accId) {
        return accountService.getAccountById(accId);
    }
}
