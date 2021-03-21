package com.kakaopay.codingTest.controller;

import com.kakaopay.codingTest.model.InvestmentRequest;
import com.kakaopay.codingTest.model.InvestmentResponse;
import com.kakaopay.codingTest.model.OrderRequest;
import com.kakaopay.codingTest.service.InvestService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/kakaopay")
public class InvestController {

    @Resource(name = "investService")
    private InvestService investService;

    // 투자하기
    @PostMapping("/orders")
    public ResponseEntity<String> invest(@RequestHeader(name = "X-USER-ID") Long userId, @RequestBody OrderRequest request){
        String result = this.investService.invest(userId, request.getProductId(), request.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 전체 투자 상품 조회
    @GetMapping("/investments")
    public ResponseEntity<List<InvestmentResponse>> list(){
        return ResponseEntity.ok(this.investService.list());
    }

    // 나의 투자 상품 조회
    @GetMapping("/user/investments")
    public ResponseEntity<List<InvestmentResponse>> detail(@RequestHeader(name = "X-USER-ID") Long userId){
        return ResponseEntity.ok(this.investService.getUserInfo(userId));
    }

    // 투자 상품 등록
    @PostMapping("/investments")
    public ResponseEntity<String> itemAdd(@RequestBody InvestmentRequest request) {
        this.investService.itemAdd(request.getTitle(), request.getInvestmentAmount(), request.getStartedAt(), request.getFinishedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(request.getTitle() + " 상품이 등록되었습니다.");
    }

    // 사용자 등록
    @PostMapping("/users")
    public ResponseEntity<String> userAdd(@RequestHeader(name = "X-USER-ID") Long userId){
        this.investService.userAdd(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("사용자 등록에 성공하였습니다. : " + userId);
    }

    // 샘플 생성
    @GetMapping("/sample")
    public void sample(){
        this.investService.makeSample();
    }

}
