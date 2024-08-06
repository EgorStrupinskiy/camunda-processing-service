package com.azati.warshipprocessing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "session")
public class Session {

    @Id
    @NonNull
    @Column(name = "id")
    private UUID id;

    @NonNull
    @Column(name = "first_user_id")
    private String firstUserId;

    @Column(name = "second_user_id")
    private String secondUserId;

    @NonNull
    @Column(name = "creation_date")
    private Instant creationDate;
}