package com.kakaopay.codingTest.repository;

import com.kakaopay.codingTest.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    @Query("select i from Investment i where i.startedAt <= current_timestamp and i.finishedAt >= current_timestamp")
    List<Investment> findAllByCurrentTimestamp();

}
