package com.vietlott.lottery.vietlott.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mega-645")
public class Mega645ViewController {

    @GetMapping
    public String page() {
        return "mega-645";
    }
}
