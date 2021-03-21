package com.kakaopay.codingTest.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "investment")
public class Investment {

    // 상품 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    // 상품 제목
    @Column(nullable = false)
    private String title;

    // 총 모집 금액
    @Column(nullable = false)
    private Integer totalInvestingAmount;

    // 현재 모집 금액
    @Builder.Default
    private Integer currentInvestingAmount = 0;

    // 투자 모집 상태( 모집 중 : Y, 모집 완료 : N )
    private String investStatus;

    // 투자 시작 일시(상품 모집 기간)
    @Column(nullable = false)
    private Date startedAt;

    // 투자 종료 일시(상품 모집 기간)
    @Column(nullable = false)
    private Date finishedAt;

    // 해당 상품에 투자한 사용자
    // 투자자 수는 List 길이를 반환

    @OneToMany(mappedBy = "investment")
    private List<Order> orders = new ArrayList<>();

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     * */
    @PrePersist
    public void prePersist() {
        this.totalInvestingAmount = this.totalInvestingAmount == null ? 0 : this.totalInvestingAmount;
        this.currentInvestingAmount = this.currentInvestingAmount == null ? 0 : this.currentInvestingAmount;
        this.investStatus = this.investStatus == null ? "Y" : this.investStatus;
    }

    @Override
    public String toString() {
        return "Investment{" +
                "productId=" + productId +
                ", title='" + title + '\'' +
                ", totalInvestingAmount=" + totalInvestingAmount +
                ", currentInvestingAmount=" + currentInvestingAmount +
                ", investStatus='" + investStatus + '\'' +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                '}';
    }
}
