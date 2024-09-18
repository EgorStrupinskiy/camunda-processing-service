package com.azati.warshipprocessing.repository;

import com.azati.warshipprocessing.entity.MapCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MapCellRepository extends JpaRepository<MapCell, Long> {

    @Query("SELECT map " +
            "FROM MapCell map " +
            "WHERE map.x = :x " +
            "AND map.y = :y " +
            "AND map.userId = :userId " +
            "AND map.sessionId = :sessionId")
    Optional<MapCell> findByFields(Integer x, Integer y, String userId, UUID sessionId);

    Optional<List<MapCell>> findByUserIdAndSessionId(String userId, UUID sessionId);
}

