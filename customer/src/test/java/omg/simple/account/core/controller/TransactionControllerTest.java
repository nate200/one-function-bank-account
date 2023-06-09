package omg.simple.account.core.controller;

import com.google.gson.Gson;
import omg.simple.account.core.exception.business.BadRequestParameterException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import omg.simple.account.core.model.business.Transaction;
import omg.simple.account.core.service.TransferManager;

import static java.math.BigDecimal.TEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    final Gson GSON = new Gson();
    final Transaction DEFAULT_TRANSACTION = Transaction.builder().currency("CHF").fromAcc(1L).toAcc(2L).amount(TEN).build();

    MockHttpServletRequestBuilder POST_TRANSFER_TO_WITH_IN_APP = post("/transfer-with-in-app")
            .contentType(MediaType.APPLICATION_JSON)
            .content(GSON.toJson(DEFAULT_TRANSACTION));

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransferManager transferManager;

    @Test
    void transferToWithInApp_200() throws Exception {
        this.mvc.perform(POST_TRANSFER_TO_WITH_IN_APP)
                .andExpect(status().isOk());
    }
    @Test
    void transferToWithInApp_400() throws Exception {
        Mockito.doThrow(new BadRequestParameterException("bruh")).when(transferManager).transferWithInApp(any(Transaction.class));
        this.mvc.perform(POST_TRANSFER_TO_WITH_IN_APP)
                .andExpect(status().isBadRequest());
    }
}