package com.kaio.limbus_gacha_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "player")
@Getter @Setter @NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private int currency = 13000;

    @ManyToMany
    @JoinTable(
            name = "player_identity",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "identity_id")
    )
    private Set<Identity> ownedIdentities = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "player_ego",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "ego_id")
    )
    private Set<Ego> ownedEgos = new HashSet<>();


}
