package omg.simple.account.core.config;

import omg.simple.account.security.config.SecurityFilterConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ SecurityFilterConfig.class})
public class CoreSecurityFilterConfig {

}
/*@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityFilterConfig {

    JwtRequestFilter filter;
    UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return http
                //.cors().disable()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/auth/**")
                .permitAll().anyRequest().authenticated()
                .and()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .build();
    }

}*/
