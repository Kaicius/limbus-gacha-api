package com.kaio.limbus_gacha_api.repository;

import com.kaio.limbus_gacha_api.entity.PlayerBanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerBannerRepository extends JpaRepository<PlayerBanner, Long> {
    Optional<PlayerBanner> findByPlayerIdAndBannerId(Long playerId, Long bannerId);
}
