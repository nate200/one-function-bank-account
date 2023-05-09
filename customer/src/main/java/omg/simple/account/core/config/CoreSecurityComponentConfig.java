package omg.simple.account.core.config;

import lombok.AllArgsConstructor;
import omg.simple.account.core.service.UserAccountService;
import omg.simple.account.security.component.JwtRequestFilter;
import omg.simple.account.security.component.JwtUtil;
import omg.simple.account.security.config.SecurityComponentConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@AllArgsConstructor
@Import({ SecurityComponentConfig.class})
public class CoreSecurityComponentConfig {
    JwtUtil jwtUtil;
    UserAccountService userAccService;

    @Bean
    public UserDetailsService userDetailsService(){
        return userEmail -> userAccService.getAccountByEmail(userEmail);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(){
        return new JwtRequestFilter(jwtUtil, userDetailsService());
    }
}
