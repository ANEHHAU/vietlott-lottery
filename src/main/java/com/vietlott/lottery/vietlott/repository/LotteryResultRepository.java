package com.vietlott.lottery.vietlott.repository;

import com.vietlott.lottery.vietlott.entity.LotteryDraw;
import com.vietlott.lottery.vietlott.entity.LotteryResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LotteryResultRepository extends JpaRepository<LotteryResult, Long> {
    List<LotteryResult> findByDraw(LotteryDraw draw);
}