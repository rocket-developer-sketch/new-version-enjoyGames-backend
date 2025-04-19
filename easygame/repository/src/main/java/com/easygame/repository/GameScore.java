package com.easygame.repository;

import com.easygame.repository.type.GameType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game_scores")
@Entity
public class GameScore extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;

    @Column(nullable = false, length = 30)
    private String nickName;

    @Column(nullable = false, length = 36)
    private String jti;

    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameType gameType;

    @Builder
    public GameScore(Long scoreId, String nickName, String jti, int score, GameType gameType) {
        if(scoreId != null) {
            this.scoreId = scoreId;
        }
        if (nickName == null || nickName.trim().isEmpty()) {
            throw new IllegalArgumentException("nickName must not be null or empty");
        }
        if (jti == null || jti.trim().isEmpty()) {
            throw new IllegalArgumentException("jti must not be null or empty");
        }
        if (gameType == null) {
            throw new IllegalArgumentException("gameType must not be null");
        }

        this.score = score;
        this.nickName = nickName;
        this.jti = jti;
        this.gameType = gameType;
    }

    @Override
    public String toString() {
        return "GameScore{" +
                "scoreId=" + scoreId +
                ", nickName='" + nickName + '\'' +
                ", jti='" + jti +'\'' +
                ", score=" + score +
                ", gameType=" + gameType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GameScore gameScore = (GameScore) o;
        return score == gameScore.score && Objects.equals(scoreId, gameScore.scoreId) && Objects.equals(nickName, gameScore.nickName) && Objects.equals(jti, gameScore.jti) && gameType == gameScore.gameType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scoreId, nickName, jti, score, gameType);
    }
}
