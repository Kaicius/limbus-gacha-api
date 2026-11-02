package com.kaio.limbus_gacha_api.dto;

import com.kaio.limbus_gacha_api.entity.Identity;
import com.kaio.limbus_gacha_api.entity.Player;

import java.util.Comparator;
import java.util.List;

public record PlayerResponse(
        Long id, String username, int currency,
        List<IdentityResponse> identities,
        List<EgoResponse> egos
) {
    public static PlayerResponse from(Player p) {
        return new PlayerResponse(
                p.getId(), p.getUsername(), p.getCurrency(),
                p.getOwnedIdentities().stream()
                        .sorted(Comparator.comparing(Identity::getRarity).thenComparing(Identity::getName))
                        .map(IdentityResponse::from)
                        .toList(),
                p.getOwnedEgos().stream().map(EgoResponse::from).toList()
        );
    }
}