package com.kaio.limbus_gacha_api.repository;

import com.kaio.limbus_gacha_api.entity.Ego;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EgoRepository extends JpaRepository<Ego, Long> {
    long count();
}