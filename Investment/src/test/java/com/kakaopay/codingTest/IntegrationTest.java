package com.kakaopay.codingTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kakaopay.codingTest.common.util.DateUtils;
import com.kakaopay.codingTest.testclass.InvestmentRequestForTest;
import com.kakaopay.codingTest.entity.Investment;
import com.kakaopay.codingTest.entity.User;
import com.kakaopay.codingTest.model.InvestmentRequest;
import com.kakaopay.codingTest.model.OrderRequest;
import com.kakaopay.codingTest.repository.InvestmentRepository;
import com.kakaopay.codingTest.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired protected MockMvc mvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired private InvestmentRepository investmentRepository;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        // 1. 유저 등록
        Long userId = 1L;
        User user = User.builder()
                .userId(userId)
                .investmentAmount(0)
                .build();

        userRepository.save(user);
        userRepository.flush();
        // 2. 상품 등록
        Investment investment = Investment.builder()
                .productId(1L)
                .startedAt(new Date())
                .finishedAt(DateUtils.addDays(new Date(), 30))
                .title("부동산")
                .totalInvestingAmount(10000000)
                .build();
        investmentRepository.save(investment);
        investmentRepository.flush();
    }

    @Test
    public void investmentListSuccess() throws Exception {
        mvc.perform(get("/kakaopay/investments"))
            .andDo(print());
    }

    @Test
    public void investmentDateStrCreateSuccess() throws Exception {
        // given
        String title = "부동산";
        Integer investingAmount = 100000;
        InvestmentRequestForTest request = new InvestmentRequestForTest(title, investingAmount, "2021-03-10", "2021-03-21");

        mvc.perform(post("/kakaopay/investments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").isString())
            .andExpect(jsonPath("$").value(request.getTitle() + " 상품이 등록되었습니다."))
            .andDo(print());
    }

    @Test
    public void investmentCreateSuccess() throws Exception {
        // given
        String title = "부동산";
        Integer investingAmount = 100000;
        InvestmentRequest request = new InvestmentRequest(title, investingAmount, new Date(), DateUtils.addDays(new Date(), 30));

        mvc.perform(post("/kakaopay/investments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isString())
                .andExpect(jsonPath("$").value(request.getTitle() + " 상품이 등록되었습니다."))
                .andDo(print());
    }

    @Test
    public void userCreateSuccess() throws Exception {
        Long userId = 2L;

        mvc.perform(post("/kakaopay/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", objectMapper.writeValueAsString(userId))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").isString())
            .andExpect(jsonPath("$").value("사용자 등록에 성공하였습니다. : " + userId))
            .andDo(print());
    }

    @Test
    public void orderCreateSuccess() throws Exception {

        Investment investment = Investment.builder()
                .productId(3L)
                .startedAt(new Date())
                .finishedAt(DateUtils.addDays(new Date(), 30))
                .title("부동산")
                .totalInvestingAmount(10000000)
                .build();
        investmentRepository.save(investment);
        investmentRepository.flush();

        // 투자하기
        Integer amount = 100000;
        OrderRequest request = new OrderRequest(investment.getProductId(), amount);

        mvc.perform(post("/kakaopay/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", objectMapper.writeValueAsString(1L))
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").value(amount))
                .andDo(print());
    }

    @Test
    public void myListSuccess() throws Exception {

        // 투자하기
        Integer amount = 100000;
        OrderRequest request = new OrderRequest(2L, amount);

        mvc.perform(post("/kakaopay/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", objectMapper.writeValueAsString(1L))
                .content(objectMapper.writeValueAsString(request))
        );
        // 내 투자 상품 조회하기
        mvc.perform(get("/kakaopay/user/investments")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", objectMapper.writeValueAsString(1L))
        )
        .andDo(print());
    }
}
