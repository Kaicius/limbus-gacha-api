package com.kaio.limbus_gacha_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "player_banner",
        uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "banner_id"})
)
@Getter @Setter @NoArgsConstructor
public class PlayerBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(optional = false)
    @JoinColumn(name = "banner_id")
    private Banner banner;

    @Column(nullable = false)
    private int pity = 0;
}