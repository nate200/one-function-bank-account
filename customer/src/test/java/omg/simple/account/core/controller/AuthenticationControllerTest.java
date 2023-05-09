package omg.simple.account.core.controller;

import com.google.gson.Gson;
import omg.simple.account.core.exception.business.BadRequestParameterException;
import omg.simple.account.core.model.api.AuthenticationRequest;
import omg.simple.account.core.model.api.AuthenticationResponse;
import omg.simple.account.core.model.api.RegisterRequest;
import omg.simple.account.core.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    AuthenticationService authService;

    final Gson GSON = new Gson();
    RegisterRequest REGISTER_REQ = RegisterRequest.builder().build();
    AuthenticationRequest AUTH_REQ = AuthenticationRequest.builder().build();

    MockHttpServletRequestBuilder POST_REGISTER = post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(GSON.toJson(REGISTER_REQ));

    MockHttpServletRequestBuilder POST_AUTHENTICATE = post("/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(GSON.toJson(AUTH_REQ));

    @Test
    void register_200() throws Exception {
        given(authService.register(any(RegisterRequest.class))).willReturn(new AuthenticationResponse("aaa"));

        this.mvc.perform(POST_REGISTER)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"access_token\"")));
    }

    @Test
    void register_400() throws Exception {
        given(authService.register(any(RegisterRequest.class))).willThrow(new BadRequestParameterException("aaa"));

        this.mvc.perform(POST_REGISTER)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("\"BAD_REQUEST\"")));
    }

    @Test
    void authenticate_200() throws Exception {
        given(authService.authenticate(any(AuthenticationRequest.class))).willReturn(new AuthenticationResponse("aaa"));

        this.mvc.perform(POST_AUTHENTICATE)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"access_token\"")));
    }

    @Test
    void authenticate_500() throws Exception {
        given(authService.authenticate(any(AuthenticationRequest.class))).willThrow(UsernameNotFoundException.class);

        this.mvc.perform(POST_AUTHENTICATE)
            .andExpect(status().isInternalServerError());
                //.andExpect(status().isNotFound());
        //.andExpect(content().string(containsString("\"BAD_REQUEST\"")));
    }
}