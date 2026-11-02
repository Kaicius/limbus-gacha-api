package com.kaio.limbus_gacha_api.service;

import com.kaio.limbus_gacha_api.dto.BannerResponse;
import com.kaio.limbus_gacha_api.expection.ResourceNotFoundException;
import com.kaio.limbus_gacha_api.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<BannerResponse> findAllActive() {
        return bannerRepository.findByActiveTrue().stream()
                .map(BannerResponse::from)
                .toList();
    }

    public BannerResponse findById(Long id) {
        return bannerRepository.findById(id)
                .map(BannerResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Banner " + id + " não encontrado"));
    }
}