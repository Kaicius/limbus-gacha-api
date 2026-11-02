package com.kaio.limbus_gacha_api.controller;

import com.kaio.limbus_gacha_api.dto.LoginRequest;
import com.kaio.limbus_gacha_api.dto.PityResponse;
import com.kaio.limbus_gacha_api.dto.PlayerResponse;
import com.kaio.limbus_gacha_api.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/login")
    public PlayerResponse login(@RequestBody @Valid LoginRequest req) {
        return playerService.login(req);
    }

    @GetMapping("/{id}")
    public PlayerResponse findById(@PathVariable Long id) {
        return playerService.findById(id);
    }

    @GetMapping("/{playerId}/banners/{bannerId}/pity")
    public PityResponse getPity(@PathVariable Long playerId, @PathVariable Long bannerId) {
        return playerService.getPity(playerId, bannerId);
    }

    @PostMapping("/{playerId}/identities/{identityId}/uptie")
    public PlayerResponse uptie(@PathVariable Long playerId, @PathVariable Long identityId) {
        return playerService.uptie(playerId, identityId);
    }

    @PostMapping("/{id}/currency")
    public PlayerResponse addCurrency(@PathVariable Long id) {
        return playerService.addCurrency(id);
    }
}