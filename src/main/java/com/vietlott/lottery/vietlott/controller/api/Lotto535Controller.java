package com.vietlott.lottery.vietlott.controller.api;

import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;


@RestController
@RequestMapping("/api/lotto-535")
public class Lotto535Controller {

    private static final SecureRandom secureRandom =
            new SecureRandom(UUID.randomUUID().toString().getBytes());

    @GetMapping("/predict")
    public List<Integer> predict() {
        List<Integer> pool = new ArrayList<>();
        for (int i = 1; i <= 35; i++) pool.add(i);

        Collections.shuffle(pool, secureRandom);

        List<Integer> result = new ArrayList<>(pool.subList(0, 5));
        Collections.sort(result);

        int special = secureRandom.nextInt(12) + 1;
        result.add(special);

        return result;
    }

}
