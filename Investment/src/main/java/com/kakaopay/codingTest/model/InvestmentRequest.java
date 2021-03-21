package com.kakaopay.codingTest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentRequest {

    private String title;
    private Integer investmentAmount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date finishedAt;
}
