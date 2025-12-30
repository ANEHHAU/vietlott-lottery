package com.vietlott.lottery.vietlott.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lottery_result",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"draw_id", "number"})
        },
        indexes = {
                @Index(name = "idx_result_draw", columnList = "draw_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotteryResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Kỳ quay
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "draw_id", nullable = false)
    private LotteryDraw draw;

    // Số trúng
    @Column(nullable = false)
    private int number;

    // Vị trí / thứ tự (giải đặc biệt, giải nhất...)
    private int position;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
