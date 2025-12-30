package com.vietlott.lottery.vietlott.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lottery_prediction",
        indexes = {
                @Index(name = "idx_prediction_game", columnList = "game_id"),
                @Index(name = "idx_prediction_draw", columnList = "draw_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotteryPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Loại game
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private LotteryGame game;

    // 🔗 Kỳ quay (có thể null nếu dự đoán trước)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id")
    private LotteryDraw draw;

    // Số dự đoán
    @Column(nullable = false)
    private int number;

    // Nguồn dự đoán (AI, thống kê, random...)
    @Column(length = 50)
    private String source;

    // Độ tin cậy (%)
    private int confidence;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
