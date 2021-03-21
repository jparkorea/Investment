package com.kakaopay.codingTest.model;

import com.kakaopay.codingTest.entity.Investment;
import lombok.*;

import java.util.*;

@Data
public class InvestmentResponse {

    private Long productId;

    private String title;

    private Integer totalInvestingAmount;

    private Integer currentInvestingAmount;

    private String investStatus;

    private Date startedAt;

    private Date finishedAt;

    private Integer orderIds;

    private Integer myAmount;

    private Date investDate;

    public InvestmentResponse(){}

    public static InvestmentResponse of(Investment investment) {
        InvestmentResponse investmentResponse = new InvestmentResponse();
        investmentResponse.setProductId(investment.getProductId());
        investmentResponse.setTitle(investment.getTitle());
        investmentResponse.setTotalInvestingAmount(investment.getTotalInvestingAmount());
        investmentResponse.setCurrentInvestingAmount(investment.getCurrentInvestingAmount());
        investmentResponse.setInvestStatus(investment.getInvestStatus());
        investmentResponse.setStartedAt(investment.getStartedAt());
        investmentResponse.setFinishedAt(investment.getFinishedAt());
        investmentResponse.setOrderIds(investment.getOrders() == null ? 0 : investment.getOrders().size());
        return investmentResponse;
    }


}