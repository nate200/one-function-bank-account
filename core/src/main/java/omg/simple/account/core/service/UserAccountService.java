package omg.simple.account.core.service;

import lombok.AllArgsConstructor;
import omg.simple.account.core.model.business.UserAccount;
import omg.simple.account.core.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserAccountService   {
    UserAccountRepository userAccRepo;

    public UserAccount getAccountByEmail(String email) throws UsernameNotFoundException {
        return userAccRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("not user account with email: " + email));
    }
}
