package com.azati.warshipprocessing.repository;

import com.azati.warshipprocessing.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    @Query("SELECT s FROM Session s WHERE s.secondUserId IS NULL ")
    List<Session> findOpenSessions();

}
