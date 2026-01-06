package com.vietlott.lottery.vietlott.controller.api;

import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;

@RestController
@RequestMapping("/api/mega-645")
public class Mega645Controller {

    private static final SecureRandom secureRandom =
            new SecureRandom(UUID.randomUUID().toString().getBytes());

    @GetMapping("/predict")
    public List<Integer> predict() {

        List<Integer> pool = new ArrayList<>(45);
        for (int i = 1; i <= 45; i++) {
            pool.add(i);
        }

        // Fisher–Yates shuffle với SecureRandom
        Collections.shuffle(pool, secureRandom);

        List<Integer> result = pool.subList(0, 6);
        Collections.sort(result);

        return result;
    }
}
