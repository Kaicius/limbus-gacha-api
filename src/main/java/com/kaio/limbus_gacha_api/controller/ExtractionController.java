package com.kaio.limbus_gacha_api.controller;

import com.kaio.limbus_gacha_api.dto.ExtractionResponse;
import com.kaio.limbus_gacha_api.service.ExtractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExtractionController {

    private final ExtractionService extractionService;

    @PostMapping("/players/{playerId}/banners/{bannerId}/extractions")
    public ExtractionResponse extract(@PathVariable Long playerId,
                                      @PathVariable Long bannerId,
                                      @RequestParam(defaultValue = "1") int amount) {
        return extractionService.extract(playerId, bannerId, amount);
    }
}