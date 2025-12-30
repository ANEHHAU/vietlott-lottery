package com.vietlott.lottery.vietlott.repository;

import com.vietlott.lottery.vietlott.entity.LotteryDraw;
import com.vietlott.lottery.vietlott.entity.LotteryGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface LotteryDrawRepository
        extends JpaRepository<LotteryDraw, Long> {

    Optional<LotteryDraw> findByGameAndDrawDateAndDrawTime(
        LotteryGame game,
        LocalDate date,
        LocalTime time
    );
}
