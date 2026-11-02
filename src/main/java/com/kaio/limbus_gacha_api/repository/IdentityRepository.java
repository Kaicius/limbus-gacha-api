package com.kaio.limbus_gacha_api.repository;

import com.kaio.limbus_gacha_api.entity.Identity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityRepository extends JpaRepository<Identity, Long> {
    long count();
}