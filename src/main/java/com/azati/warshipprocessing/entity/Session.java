package com.azati.warshipprocessing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @Column(name = "first_user_id", nullable = false)
    private String firstUserId;

    @Column(name = "second_user_id")
    private String secondUserId;

    @NotNull
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Instant creationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = Instant.now();
    }
}
