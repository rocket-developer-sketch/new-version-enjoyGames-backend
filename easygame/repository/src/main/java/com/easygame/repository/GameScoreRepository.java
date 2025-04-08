package com.easygame.repository;

import com.easygame.repository.type.GameType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameScoreRepository extends JpaRepository<GameScore, Long>, CustomGameScoreRepository {
    @Query("SELECT gs FROM GameScore gs JOIN FETCH gs.user WHERE gs.gameType = :gameType")
    List<GameScore> findByGameType(@Param("gameType") GameType gameType, Pageable pageable);
}
