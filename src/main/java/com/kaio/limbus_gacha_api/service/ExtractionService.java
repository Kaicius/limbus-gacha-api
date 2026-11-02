package com.kaio.limbus_gacha_api.service;

import com.kaio.limbus_gacha_api.dto.*;
import com.kaio.limbus_gacha_api.entity.*;
import com.kaio.limbus_gacha_api.enums.Rarity;
import com.kaio.limbus_gacha_api.expection.ResourceNotFoundException;
import com.kaio.limbus_gacha_api.repository.BannerRepository;
import com.kaio.limbus_gacha_api.repository.PlayerBannerRepository;
import com.kaio.limbus_gacha_api.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtractionService {

    public static final int COST_PER_PULL = 130;

    private static final double RATE_EGO = 1.3;
    private static final double RATE_TRIPLE = 2.9;
    private static final double RATE_DOUBLE = 12.8;
    private static final double RATE_TRIPLE_NO_EGO = 3.0;
    private static final double RATE_DOUBLE_NO_EGO = 13.0;

    private enum Outcome { ZERO, DOUBLE, TRIPLE, EGO }

    private final PlayerRepository playerRepository;
    private final BannerRepository bannerRepository;
    private final PlayerBannerRepository playerBannerRepository;

    public ExtractionResponse extract(Long playerId, Long bannerId, int amount) {
        if (amount != 1 && amount != 10) {
            throw new IllegalArgumentException("amount deve ser 1 ou 10");
        }

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player " + playerId + " não encontrado"));
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Banner " + bannerId + " não encontrado"));

        if (!banner.isActive()) {
            throw new IllegalArgumentException("Este banner não está ativo");
        }

        int cost = COST_PER_PULL * amount;
        if (player.getCurrency() < cost) {
            throw new IllegalArgumentException(
                    "Lunacy insuficiente: precisa de " + cost + ", tem " + player.getCurrency());
        }
        player.setCurrency(player.getCurrency() - cost);

        PlayerBanner playerBanner = playerBannerRepository.findByPlayerIdAndBannerId(playerId, bannerId)
                .orElseGet(() -> {
                    PlayerBanner novo = new PlayerBanner();
                    novo.setPlayer(player);
                    novo.setBanner(banner);
                    return novo;
                });

        List<PullResult> results = new ArrayList<>();
        for (int position = 1; position <= amount; position++) {
            playerBanner.setPity(playerBanner.getPity() + 1);
            boolean guaranteed = playerBanner.getPity() >= PlayerService.PITY_LIMIT;
            if (guaranteed) {
                playerBanner.setPity(0);
            }

            boolean egoAvailable = !player.getOwnedEgos().containsAll(banner.getEgos());

            Outcome outcome = roll(guaranteed, egoAvailable);
            results.add(deliver(player, banner, outcome, position, guaranteed));
        }

        playerRepository.save(player);
        playerBannerRepository.save(playerBanner);

        return new ExtractionResponse(
                results,
                playerBanner.getPity(),
                PlayerService.PITY_LIMIT - playerBanner.getPity(),
                player.getCurrency()
        );
    }

    private Outcome roll(boolean guaranteed, boolean egoAvailable) {
        double r = ThreadLocalRandom.current().nextDouble(100);

        double ego = egoAvailable ? RATE_EGO : 0;
        double triple = egoAvailable ? RATE_TRIPLE : RATE_TRIPLE_NO_EGO;
        double dbl = egoAvailable ? RATE_DOUBLE : RATE_DOUBLE_NO_EGO;

        if (r < ego) return Outcome.EGO;
        if (r < ego + triple) return Outcome.TRIPLE;
        if (guaranteed) return Outcome.DOUBLE;
        if (r < ego + triple + dbl) return Outcome.DOUBLE;
        return Outcome.ZERO;
    }

    private PullResult deliver(Player player, Banner banner, Outcome outcome, int position, boolean guaranteed) {
        if (outcome == Outcome.EGO) {
            Ego ego = pickRandom(new ArrayList<>(banner.getEgos()));
            boolean isNew = player.getOwnedEgos().add(ego);   // add devolve false se já tinha
            return new PullResult(position, "EGO", isNew, guaranteed, null, EgoResponse.from(ego));
        }

        Rarity rarity = Rarity.valueOf(outcome.name());
        List<Identity> pool = banner.getIdentities().stream()
                .filter(i -> i.getRarity() == rarity)
                .toList();

        Identity identity = pickRandom(pool);
        boolean isNew = player.getOwnedIdentities().add(identity);
        return new PullResult(position, rarity.name(), isNew, guaranteed, IdentityResponse.from(identity), null);
    }

    private <T> T pickRandom(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}