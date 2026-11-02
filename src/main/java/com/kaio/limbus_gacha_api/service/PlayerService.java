package com.kaio.limbus_gacha_api.service;

import com.kaio.limbus_gacha_api.dto.LoginRequest;
import com.kaio.limbus_gacha_api.dto.PityResponse;
import com.kaio.limbus_gacha_api.dto.PlayerResponse;
import com.kaio.limbus_gacha_api.entity.Player;
import com.kaio.limbus_gacha_api.entity.PlayerBanner;
import com.kaio.limbus_gacha_api.expection.ResourceNotFoundException;
import com.kaio.limbus_gacha_api.repository.BannerRepository;
import com.kaio.limbus_gacha_api.repository.PlayerBannerRepository;
import com.kaio.limbus_gacha_api.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {

    public static final int PITY_LIMIT = 10;
    public static final int CURRENCY_REWARD = 1300; // um 10x

    private final PlayerRepository playerRepository;
    private final PlayerBannerRepository playerBannerRepository;
    private final BannerRepository bannerRepository;

    public PlayerResponse login(LoginRequest req) {
        String username = req.username().trim().toLowerCase();

        Player player = playerRepository.findByUsername(username)
                .orElseGet(() -> {
                    Player novo = new Player();
                    novo.setUsername(username);
                    return playerRepository.save(novo);
                });

        return PlayerResponse.from(player);
    }

    @Transactional(readOnly = true)
    public PlayerResponse findById(Long id) {
        return PlayerResponse.from(findPlayer(id));
    }

    @Transactional(readOnly = true)
    public PityResponse getPity(Long playerId, Long bannerId) {
        findPlayer(playerId);

        if (!bannerRepository.existsById(bannerId)) {
            throw new ResourceNotFoundException("Banner " + bannerId + " não encontrado");
        }

        int pity = playerBannerRepository.findByPlayerIdAndBannerId(playerId, bannerId)
                .map(PlayerBanner::getPity)
                .orElse(0); // nunca girou nesse banner

        return new PityResponse(bannerId, pity, PITY_LIMIT - pity);
    }

    public PlayerResponse addCurrency(Long playerId) {
        Player player = findPlayer(playerId);
        player.setCurrency(player.getCurrency() + CURRENCY_REWARD);
        return PlayerResponse.from(playerRepository.save(player));
    }

    private Player findPlayer(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player " + id + " não encontrado"));
    }
}