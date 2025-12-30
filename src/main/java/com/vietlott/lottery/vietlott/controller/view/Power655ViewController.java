package com.vietlott.lottery.vietlott.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/power-655")
public class Power655ViewController {

    @GetMapping
    public String page() {
        return "power-655";
    }
}
