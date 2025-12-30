package com.vietlott.lottery.vietlott.controller.api;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/max-3d-pro")
public class Max3DProController {

    @GetMapping("/predict")
    public List<Integer> predict() {
        Random random = new Random();
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            result.add(random.nextInt(10));
        }

        return result;
    }
}
