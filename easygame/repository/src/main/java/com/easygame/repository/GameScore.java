package com.easygame.repository;

import com.easygame.repository.type.GameType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "gamescore")
@Entity
public class GameScore extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;

    @Column(nullable = false, length = 30)
    private String nickName;

    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameType gameType;

    @Builder
    public GameScore(Long scoreId, String nickName, int score, GameType gameType) {
        if(scoreId != null) {
            this.scoreId = scoreId;
        }

        this.nickName = nickName;
        this.score = score;
        this.gameType = gameType;
    }

    @Override
    public String toString() {
        return "GameScore{" +
                "scoreId=" + scoreId +
                ", nickName='" + nickName + '\'' +
                ", score=" + score +
                ", gameType=" + gameType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GameScore gameScore = (GameScore) o;
        return score == gameScore.score && Objects.equals(scoreId, gameScore.scoreId) && Objects.equals(nickName, gameScore.nickName) && gameType == gameScore.gameType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scoreId, nickName, score, gameType);
    }
}
