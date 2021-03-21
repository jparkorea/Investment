package com.kakaopay.codingTest.service;

import com.kakaopay.codingTest.common.exception.InvalidValueException;
import com.kakaopay.codingTest.common.exception.SoldOutException;
import com.kakaopay.codingTest.entity.Investment;
import com.kakaopay.codingTest.entity.Order;
import com.kakaopay.codingTest.entity.User;
import com.kakaopay.codingTest.model.InvestmentResponse;
import com.kakaopay.codingTest.repository.InvestmentRepository;
import com.kakaopay.codingTest.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
public class InvestService {

    @Resource
    private InvestmentRepository investmentRepository;

    @Resource
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    /**
     * 사용자 추가
     * @param userId
     */
    @Transactional
    public void userAdd(Long userId){
        User user = User.builder()
                .userId(userId)
                .build();

        this.userRepository.save(user);
    }

    /**
     * 투자 상품 추가
     * @param title
     * @param investingAmount
     * @param startedAt
     * @param finishedAt
     */
    @Transactional
    public void itemAdd(String title, Integer investingAmount,
                        Date startedAt, Date finishedAt){
        Investment investment = Investment.builder()
                .title(title)
                .totalInvestingAmount(investingAmount)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .build();

        this.investmentRepository.save(investment);
    }

    /**
     * 전체 투자 상품 조회
     * @return
     */
    public List<InvestmentResponse> list() {
        List<Investment> investmentList = this.investmentRepository.findAllByCurrentTimestamp();
        ArrayList<InvestmentResponse> responses = new ArrayList<>();

        for(Investment investment : investmentList){
            InvestmentResponse response = new InvestmentResponse();

            response.setProductId(investment.getProductId());
            response.setTitle(investment.getTitle());
            response.setTotalInvestingAmount(investment.getTotalInvestingAmount());
            response.setCurrentInvestingAmount(investment.getCurrentInvestingAmount());
            response.setInvestStatus(investment.getInvestStatus());
            response.setStartedAt(investment.getStartedAt());
            response.setFinishedAt(investment.getFinishedAt());
            response.setOrderIds(investment.getOrders() == null ? 0 : investment.getOrders().size());

            responses.add(response);
        }

        return responses;
    }

    /**
     * 투자하기
     * @param userId
     * @param productId
     * @param amount
     * @return
     */
    @Transactional
    public String invest(Long userId, Long productId, Integer amount){

        String result = "";

        if(amount < 0){
            throw new InvalidValueException();
        }

        //투자 테이블에 투자 금액 올리면서 총 투자금액 넘쳤는지 체크
        User user = this.userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid userId : " + userId));
        Investment investment = this.investmentRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid productId : " + productId));

        Integer totalAmount = investment.getTotalInvestingAmount();
        Integer currentAmount = investment.getCurrentInvestingAmount();

        // 투자 가능한 상태인지 확인
        if(currentAmount + amount > totalAmount){
            //TODO :  총 모집금액까지 남은 금액만 채우고 나머지는 반환하여야 하는가?

            if (investment.getInvestStatus().equals("Y")) {
                investment.setInvestStatus("N");
                this.investmentRepository.save(investment);
            }

            // result = "모집 금액이 초과되어 " + investment.getTitle() + "상품에 투자할 수 없습니다."
            // sold-out 상태로 반환
            throw new SoldOutException();

        }else{  // 투자 가능한 상태일 때
            //투자가 진행되고, 금액이 초과되면 투자모집상태->모집완료 로 변경
            investment.setCurrentInvestingAmount(amount + investment.getCurrentInvestingAmount());
            if(investment.getTotalInvestingAmount().intValue() == investment.getCurrentInvestingAmount().intValue()){
                investment.setInvestStatus("N");
                investment.setFinishedAt(new Date());   // 완료날짜가 미래에 있었다 하더라도 모집금액이 채워지면 현재 시간으로 변경
            }

            // 해당 투자 Id와 동일한 order가 있는지 체크
            Order newOrder = new Order();
            boolean exist = false;
            if(user.getOrders() != null && user.getOrders().size() != 0){
                for(Order order : user.getOrders()){
                    if(order.getInvestment().getProductId().equals(investment.getProductId())){
                        exist = true;
                        newOrder = order;
                    }
                }
            }

            if(exist){  // 해당 Order 업데이트
                newOrder.setInvestDate(new Date());
                newOrder.setMyAmount(newOrder.getMyAmount() + amount);
            }else {  // 새로운 Order 생성
                newOrder.setUser(user);
                newOrder.setInvestment(investment);
                newOrder.setInvestDate(new Date());
            }
            // 유저 테이블에 투자한 금액을 더해준다
            user.setInvestmentAmount(user.getInvestmentAmount() + amount);

            // DB 업데이트
            this.userRepository.save(user);
            this.investmentRepository.save(investment);
            em.persist(newOrder);

            result = amount + "원을 " + investment.getTitle() + "상품에 투자 완료하였습니다.";
        }

        return result;
    }

    /**
     * 사용자 정보 조회
     * @param userId
     * @return
     */
    public List<InvestmentResponse> getUserInfo(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid userId : " + userId));
        ArrayList<InvestmentResponse> responses = new ArrayList<>();

        // 사용자 테이블에 투자한 내역이 있을 때
        if(user.getOrders() != null && user.getOrders().size() > 0){
            for(Order order : user.getOrders()){
                InvestmentResponse response = new InvestmentResponse();
                Investment investment = order.getInvestment();
                response.setProductId(investment.getProductId());
                response.setTitle(investment.getTitle());
                response.setTotalInvestingAmount(investment.getTotalInvestingAmount());
                response.setMyAmount(order.getMyAmount());
                response.setInvestDate(order.getInvestDate());

                responses.add(response);
            }
        }

        return responses;
    }

    @Transactional
    public void makeSample(){

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 30);

        Investment invest = Investment.builder()
                .investStatus("Y")
                .title("부동산")
                .currentInvestingAmount(0)
                .productId(Long.getLong("12345678"))
                .startedAt(date)
                .finishedAt(new Date(calendar.getTimeInMillis()))
                .totalInvestingAmount(2000000)
                .build();

        Investment invest2 = Investment.builder()
                .investStatus("Y")
                .title("신용")
                .currentInvestingAmount(0)
                .productId(Long.getLong("87654321"))
                .startedAt(date)
                .finishedAt(new Date(calendar.getTimeInMillis()))
                .totalInvestingAmount(100000)
                .build();

        User user = User.builder()
                .build();

        em.persist(invest);
        em.persist(invest2);
        em.persist(user);
    }
}
