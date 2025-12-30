package com.vietlott.lottery.vietlott.repository;

import com.vietlott.lottery.vietlott.entity.LotteryGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LotteryGameRepository
        extends JpaRepository<LotteryGame, Long> {

    Optional<LotteryGame> findByCode(String code);
}
