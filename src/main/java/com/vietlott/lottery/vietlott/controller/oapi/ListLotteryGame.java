package com.vietlott.lottery.vietlott.controller.oapi;

import com.vietlott.lottery.vietlott.entity.LotteryGame;
import com.vietlott.lottery.vietlott.repository.LotteryGameRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ListLotteryGame {

    private final LotteryGameRepository lotteryGameRepository;

    public ListLotteryGame(LotteryGameRepository lotteryGameRepository) {
        this.lotteryGameRepository = lotteryGameRepository;
    }

    @GetMapping("/listLotteryGame")
    public List<LotteryGame> getAllGames() {
        return lotteryGameRepository.findAll();
    }

}
