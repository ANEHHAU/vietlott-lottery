package com.vietlott.lottery.vietlott.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/mega-645")
public class Mega645Controller {

    @GetMapping("/predict")
    public List<Integer> predict() {
        Random random = new Random();
        Set<Integer> numbers = new HashSet<>();

        while (numbers.size() < 6) {
            numbers.add(random.nextInt(45) + 1);
        }

        return numbers.stream().sorted().toList();
    }
}

