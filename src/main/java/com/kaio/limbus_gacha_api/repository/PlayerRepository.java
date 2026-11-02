package com.kaio.limbus_gacha_api.repository;

import com.kaio.limbus_gacha_api.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUsername(String username);
}