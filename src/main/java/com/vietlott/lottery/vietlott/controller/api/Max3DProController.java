package com.vietlott.lottery.vietlott.controller.api;

import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;

@RestController
@RequestMapping("/api/max-3d-pro")
public class Max3DProController {

    // SecureRandom khởi tạo 1 lần, seed động
    private static final SecureRandom secureRandom =
            new SecureRandom(UUID.randomUUID().toString().getBytes());

    @GetMapping("/predict")
    public List<Integer> predict() {
        List<Integer> result = new ArrayList<>(6);

        for (int i = 0; i < 6; i++) {
            result.add(secureRandom.nextInt(10)); // 0–9
        }

        return result;
    }
}
