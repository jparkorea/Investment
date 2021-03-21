package com.kakaopay.codingTest.testclass;

public class InvestmentRequestForTest {
    private String title;
    private Integer investmentAmount;
    private String startedAt;
    private String finishedAt;

    public InvestmentRequestForTest(String title, Integer investmentAmount, String startedAt, String finishedAt) {
        this.title = title;
        this.investmentAmount = investmentAmount;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(Integer investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(String finishedAt) {
        this.finishedAt = finishedAt;
    }
}
