package com.vietlott.lottery.vietlott.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/max-3d")
public class Max3DViewController {

    @GetMapping
    public String page() {
        return "max-3d";
    }
}
