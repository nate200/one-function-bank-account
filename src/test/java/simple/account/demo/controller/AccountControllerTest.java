package simple.account.demo.controller;


import com.google.gson.Gson;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import simple.account.demo.exception.BadRequestParameterException;
import simple.account.demo.model.Account;
import simple.account.demo.model.Transaction;
import simple.account.demo.service.AccountService;
import simple.account.demo.service.TransferManager;

import java.math.BigDecimal;

import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private AccountService accountService;

    final Gson GSON = new Gson();

    final Account DEFAULT_ACC = Account.builder().total(TEN).email("aaa@aaa.com").currency("CHF").build();

    MockHttpServletRequestBuilder POST_CREATE_ACCOUNT = post("/create-account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(GSON.toJson(DEFAULT_ACC));

    MockHttpServletRequestBuilder GET_GETACCOUNT = get("/getAccount/1");

    @Test
    void createAccount_200() throws Exception {
        this.mvc.perform(POST_CREATE_ACCOUNT)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("saved")));
    }
    @Test
    void createAccount_400() throws Exception {
        doThrow(BadRequestParameterException.class).when(accountService).createAccountRequest(DEFAULT_ACC);
        this.mvc.perform(POST_CREATE_ACCOUNT)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAccount_200() throws Exception {
        long accId = 1L;
        Account expected = DEFAULT_ACC.toBuilder().id(accId).build();
        given(accountService.getAccountById(anyLong())).willReturn(expected);

        MvcResult result = this.mvc.perform(GET_GETACCOUNT)
                .andExpect(status().isOk())
                .andReturn();

        Account actual = GSON.fromJson(result.getResponse().getContentAsString(), Account.class);
        assertThat(actual)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
    @Test
    void getAccount_404() throws Exception {
        doThrow(EntityNotFoundException.class).when(accountService).getAccountById(anyLong());
        this.mvc.perform(GET_GETACCOUNT)
                .andExpect(status().isNotFound());
    }
}
