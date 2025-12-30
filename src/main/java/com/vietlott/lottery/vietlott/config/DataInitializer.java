package com.vietlott.lottery.vietlott.config;

import com.vietlott.lottery.vietlott.entity.LotteryGame;
import com.vietlott.lottery.vietlott.repository.LotteryGameRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {





    @Bean
    CommandLineRunner initGames(LotteryGameRepository repo) {
        return args -> {

            if (repo.count() > 0) return;

            repo.save(LotteryGame.builder()
                    .code("MEGA_645")
                    .name("Mega 6/45")
                    .description("Chọn 6 số từ 01 đến 45")
                    .minNumber(1)
                    .maxNumber(45)
                    .numbersCount(6)
                    .build());

            repo.save(LotteryGame.builder()
                    .code("POWER_655")
                    .name("Power 6/55")
                    .description("Chọn 6 số từ 01 đến 55")
                    .minNumber(1)
                    .maxNumber(55)
                    .numbersCount(6)
                    .build());

            repo.save(LotteryGame.builder()
                    .code("LOTTO_535")
                    .name("Lotto 5/35")
                    .description("Chọn 5 số từ 01 đến 35 và 1 số từ 1 đến 12")
                    .minNumber(1)
                    .maxNumber(35)
                    .numbersCount(6)
                    .build());

            repo.save(LotteryGame.builder()
                    .code("MAX_3D")
                    .name("Max 3D")
                    .description("Chọn 3 chữ số")
                    .minNumber(0)
                    .maxNumber(9)
                    .numbersCount(3)
                    .build());

            repo.save(LotteryGame.builder()
                    .code("MAX_3D_PRO")
                    .name("Max 3D Pro")
                    .description("Chọn 2 cặp 3 chữ số")
                    .minNumber(0)
                    .maxNumber(9)
                    .numbersCount(6)
                    .build());

            System.out.println("✅ Init lottery games done");
        };
    }



//    @Bean
//    CommandLineRunner fixLotto535(LotteryGameRepository repo) {
//        return args -> {
//            repo.findByCode("LOTTO_635").ifPresent(game -> {
//                game.setCode("LOTTO_535");
//                game.setName("Lotto 5/35");
//                game.setDescription("Chọn 5 số chính từ 01-35 và 1 số đặc biệt từ 01-12");
//                game.setMinNumber(1);
//                game.setMaxNumber(35);
//                game.setNumbersCount(6);
//                repo.save(game);
//            });
//        };
//    }





}
