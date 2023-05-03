package omg.simple.account.security.model.security;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
  String email;
  String password;
}