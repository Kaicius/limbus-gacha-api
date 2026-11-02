package com.kaio.limbus_gacha_api.controller;

import com.kaio.limbus_gacha_api.dto.BannerResponse;
import com.kaio.limbus_gacha_api.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public List<BannerResponse> findAllActive() {
        return bannerService.findAllActive();
    }

    @GetMapping("/{id}")
    public BannerResponse findById(@PathVariable Long id) {
        return bannerService.findById(id);
    }
}