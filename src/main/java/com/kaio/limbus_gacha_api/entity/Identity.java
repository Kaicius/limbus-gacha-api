package com.kaio.limbus_gacha_api.entity;

import com.kaio.limbus_admin_api.enums.Rarity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "identity")
@Getter @Setter @NoArgsConstructor
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rarity rarity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sinner_id")
    private Sinner sinner;

    @Column(length = 500)
    private String uptie1ArtUrl;

    @Column(length = 500)
    private String uptie3ArtUrl;

    @Column(length = 500)
    private String uptieCutscene;

    @Column(length = 500)
    private String obtainmentVideoUrl;
}
