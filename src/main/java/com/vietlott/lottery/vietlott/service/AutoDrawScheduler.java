//package com.vietlott.lottery.vietlott.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class AutoDrawScheduler {
//
//    private final AutoDrawService autoDrawService;
//
//    // ===== LOTTO 5/35 — 21:00 mỗi ngày =====
//    @Scheduled(cron = "0 0 21 * * *")
//    public void lotto535() {
//        autoDrawService.generateResult("LOTTO_535");
//    }
//
//    // ===== MAX 3D & MAX 3D PRO — 18:30 mỗi ngày =====
//    @Scheduled(cron = "0 30 18 * * *")
//    public void max3d() {
//        autoDrawService.generateResult("MAX_3D");
//        autoDrawService.generateResult("MAX_3D_PRO");
//    }
//
//    // ===== MEGA 6/45 — Thứ 4, 6, CN =====
//    @Scheduled(cron = "0 30 18 ? * WED,FRI,SUN")
//    public void mega645() {
//        autoDrawService.generateResult("MEGA_645");
//    }
//
//    // ===== POWER 6/55 — Thứ 3, 5, 7 =====
//    @Scheduled(cron = "0 30 18 ? * TUE,THU,SAT")
//    public void power655() {
//        autoDrawService.generateResult("POWER_655");
//    }
//}
