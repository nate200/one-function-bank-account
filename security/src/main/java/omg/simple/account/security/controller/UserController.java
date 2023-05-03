package omg.simple.account.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("aaa")
    public String get(){
        return "aaaaa";
    }

}
