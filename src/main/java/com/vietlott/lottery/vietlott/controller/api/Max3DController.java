package com.vietlott.lottery.vietlott.controller.api;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/max-3d")
public class Max3DController {

    @GetMapping("/predict")
    public List<Integer> predict() {
        Random random = new Random();
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            result.add(random.nextInt(10));
        }

        return result;
    }
}
