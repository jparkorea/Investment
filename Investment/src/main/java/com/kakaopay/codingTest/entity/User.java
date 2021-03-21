package com.kakaopay.codingTest.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long userId;

    @Builder.Default
    private Integer investmentAmount = 0;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     * */
    @PrePersist
    public void prePersist() {
        this.investmentAmount = this.investmentAmount == null ? 0 : this.investmentAmount;
    }

}
