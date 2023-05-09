package omg.simple.account.core.model.api;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
  String email;
  String password;
}