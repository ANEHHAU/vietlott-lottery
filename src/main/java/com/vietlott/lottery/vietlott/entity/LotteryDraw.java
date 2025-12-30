package com.vietlott.lottery.vietlott.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "lottery_draw",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"game_id", "draw_date", "draw_time"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotteryDraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Game (Mega, Power, Miền Bắc...)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private LotteryGame game;

    // Mã kỳ (VD: 01234)
    @Column(name = "draw_code", length = 50)
    private String drawCode;

    // Ngày quay
    @Column(name = "draw_date", nullable = false)
    private LocalDate drawDate;

    // Giờ quay (sáng / chiều / tối)
    @Column(name = "draw_time", nullable = false)
    private LocalTime drawTime;

    // Thứ tự ca trong ngày (1, 2...)
    @Column(name = "draw_index")
    private Integer drawIndex;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
