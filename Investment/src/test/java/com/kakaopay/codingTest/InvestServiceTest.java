package com.kakaopay.codingTest;

import com.kakaopay.codingTest.common.exception.ErrorCode;
import com.kakaopay.codingTest.common.exception.SoldOutException;
import com.kakaopay.codingTest.common.util.DateUtils;
import com.kakaopay.codingTest.entity.Investment;
import com.kakaopay.codingTest.entity.Order;
import com.kakaopay.codingTest.entity.User;
import com.kakaopay.codingTest.model.InvestmentResponse;
import com.kakaopay.codingTest.repository.InvestmentRepository;
import com.kakaopay.codingTest.repository.UserRepository;
import com.kakaopay.codingTest.service.InvestService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvestServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager em;

    @InjectMocks
    private InvestService investService;

    @BeforeEach
    public void setup() {

    }

    @Test
    public void list() {
        // given
        Investment investment = Investment.builder()
            .investStatus("Y")
            .title("부동산")
            .currentInvestingAmount(0)
            .productId(Long.getLong("12345678"))
            .startedAt(new Date())
            .finishedAt(DateUtils.addDays(new Date(), 30))
            .totalInvestingAmount(2000000)
            .build();

        given(investmentRepository.findAllByCurrentTimestamp())
            .willReturn(Collections.singletonList(investment));

        // when
        List<InvestmentResponse> list = investService.list();

        // then
        assertThat(list).contains(InvestmentResponse.of(investment));
    }

    @Test
    public void returnEmptyList() {
        // given
        given(investmentRepository.findAllByCurrentTimestamp())
            .willReturn(Collections.emptyList());

        // when
        List<InvestmentResponse> list = investService.list();

        // then
        assertThat(list).isEmpty();
    }

    @Test
    public void userAddSuccess(){
        //given
        Long userId = 2L;

        //when
        User newUser=new User();
        when(userRepository.save(Mockito.any(User.class))).thenReturn(newUser);
        investService.userAdd(userId);

        //then
        verify(userRepository, times(1)).save(Mockito.any(User.class));

    }

    @Test
    public void itemAddSuccess(){
        //given
        String title = "투자상품";
        Integer investingAmount = 200000;
        Date startedAt = new Date();
        Date finishedAt = DateUtils.addDays(new Date(), 30);

        //when
        Investment newInvestment = new Investment();
        when(investmentRepository.save(Mockito.any(Investment.class))).thenReturn(newInvestment);
        investService.itemAdd(title, investingAmount, startedAt, finishedAt);

        //then
        verify(investmentRepository, times(1)).save(Mockito.any(Investment.class));
    }

    @Test
    public void investSuccess(){
        // given
        Long userId = 1L;
        Long productId = 1L;
        Integer amount = 100000;

        Optional<User> optUser = Optional.of(User.builder()
                .userId(userId)
                .build());

        Optional<Investment> optInvestment = Optional.of(Investment.builder()
                .productId(productId)
                .title("투자상품")
                .totalInvestingAmount(200000)
                .startedAt(new Date())
                .finishedAt(DateUtils.addDays(new Date(), 30))
                .build());

        given(userRepository.findById(userId)).willReturn(optUser);
        given(investmentRepository.findById(productId)).willReturn(optInvestment);


        // when
        final String result = investService.invest(optUser.get().getUserId(), optInvestment.get().getProductId(), amount);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(amount + "원을 투자상품상품에 투자 완료하였습니다.");
    }

    @Test
    public void getUserInfoSuccess(){
        // given
        Long userId = 1L;
        Long productId = 1L;
        Integer amount = 100000;

        Optional<User> optUser = Optional.of(User.builder()
                .userId(userId)
                .build());

        Optional<Investment> optInvestment = Optional.of(Investment.builder()
                .productId(productId)
                .title("투자상품")
                .totalInvestingAmount(200000)
                .startedAt(new Date())
                .finishedAt(DateUtils.addDays(new Date(), 30))
                .build());

        Order newOrder = new Order();
        newOrder.setMyAmount(amount);
        newOrder.setUser(optUser.get());
        newOrder.setInvestment(optInvestment.get());
        newOrder.setInvestDate(new Date());

        List<Order> orders = new ArrayList<>();
        orders.add(newOrder);
        optUser.get().setOrders(orders);

        given(userRepository.findById(userId)).willReturn(optUser);

        //when
        List<InvestmentResponse> list = investService.getUserInfo(optUser.get().getUserId());

        //then
        assertThat(list).isNotNull();
        assertThat(list.get(0).getProductId()).isEqualTo(optInvestment.get().getProductId());
        assertThat(list.get(0).getTitle()).isEqualTo(optInvestment.get().getTitle());
        assertThat(list.get(0).getTotalInvestingAmount()).isEqualTo(optInvestment.get().getTotalInvestingAmount());
        assertThat(list.get(0).getMyAmount()).isEqualTo(optUser.get().getOrders().get(0).getMyAmount());
        assertThat(list.get(0).getInvestDate()).isEqualTo(optUser.get().getOrders().get(0).getInvestDate());

    }

    @Test
    public void soldOutException() {
        // given
        Optional<User> optUser = Optional.of(User.builder()
                .userId(1L)
                .build());

        Optional<Investment> optInvestment = Optional.of(Investment.builder()
                .productId(1L)
                .title("투자상품")
                .investStatus("Y")
                .totalInvestingAmount(200000)
                .currentInvestingAmount(190000)
                .startedAt(new Date())
                .finishedAt(DateUtils.addDays(new Date(), 30))
                .build());

        given(userRepository.findById(1L)).willReturn(optUser);
        given(investmentRepository.findById(1L)).willReturn(optInvestment);
        // then

        assertThatExceptionOfType(SoldOutException.class)
                .isThrownBy(() -> {
                    investService.invest(optUser.get().getUserId(), optInvestment.get().getProductId(), 200000);
                });

        assertThatThrownBy(() -> {
            investService.invest(optUser.get().getUserId(), optInvestment.get().getProductId(), 200000);
                }).isInstanceOf(SoldOutException.class);


    }
}
