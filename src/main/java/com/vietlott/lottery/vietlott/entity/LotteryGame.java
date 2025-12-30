package com.vietlott.lottery.vietlott.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lottery_game")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotteryGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;

    private int numbersCount;
    private int minNumber;
    private int maxNumber;

    private String description;
}
