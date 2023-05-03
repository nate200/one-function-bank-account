
package omg.simple.account.security.service;//error3. copy paste forgot to change

import lombok.AllArgsConstructor;
import omg.simple.account.security.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import omg.simple.account.security.model.UserAccount;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@AllArgsConstructor
public class UserAccountService   {
    UserAccountRepository userAccRepo;
    
    public UserAccount getAccountByEmail(String email) throws UsernameNotFoundException {
        return userAccRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("not user account with email: " + email));
    }
    
}
