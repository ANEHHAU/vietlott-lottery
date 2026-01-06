package com.vietlott.lottery.vietlott.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "lottery_result",
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

    // ✅ DANH SÁCH SỐ TRÚNG
    @ElementCollection
    @CollectionTable(
            name = "lottery_result_numbers",
            joinColumns = @JoinColumn(name = "result_id")
    )
    @Column(name = "number")
    private List<Integer> numbers;

    // Thời điểm lưu kết quả
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
