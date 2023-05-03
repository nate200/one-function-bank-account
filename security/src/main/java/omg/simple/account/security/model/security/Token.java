package omg.simple.account.security.model.security;

import jakarta.persistence.*;
import lombok.*;
import omg.simple.account.security.model.UserAccount;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue//error2 identity can't save null, why??
    Long id;

    @Column(unique = true)
    String token;

    @Enumerated(EnumType.STRING)
    TokenType tokenType;

    boolean revoked;
    boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName="id")
    UserAccount user;
}
