package com.vietlott.lottery.vietlott.controller.api;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/power-655")
public class Power655Controller {

    @GetMapping("/predict")
    public List<Integer> predict() {
        Random random = new Random();
        Set<Integer> numbers = new HashSet<>();

        while (numbers.size() < 6) {
            numbers.add(random.nextInt(55) + 1);
        }

        return numbers.stream().sorted().toList();
    }
}
