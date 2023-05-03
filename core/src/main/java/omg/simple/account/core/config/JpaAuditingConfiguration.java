package omg.simple.account.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {
    //https://stackoverflow.com/questions/60606861/spring-boot-jpa-metamodel-must-not-be-empty-when-trying-to-run-junit-integrat
}
