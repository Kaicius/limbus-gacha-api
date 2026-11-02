package com.kaio.limbus_gacha_api.repository;

import com.kaio.limbus_gacha_api.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByActiveTrue();
}
