package com.vietlott.lottery.vietlott.controller.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/api/lotto-535")
public class Lotto535Controller {

    @GetMapping("/predict")
    public List<Integer> predict() {
        Random random = new Random();

        Set<Integer> main = new HashSet<>();
        while (main.size() < 5) {
            main.add(random.nextInt(35) + 1);
        }

        List<Integer> result = new ArrayList<>(main);
        Collections.sort(result);

        int special = random.nextInt(12) + 1;
        result.add(special);

        return result;
    }
}
