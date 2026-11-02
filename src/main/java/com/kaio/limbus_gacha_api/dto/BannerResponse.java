package com.kaio.limbus_gacha_api.dto;

import com.kaio.limbus_gacha_api.entity.Banner;
import com.kaio.limbus_gacha_api.entity.Identity;

import java.util.Comparator;
import java.util.List;

public record BannerResponse(
        Long id, String name, String thumbnailUrl, boolean active,
        List<IdentityResponse> identities,
        List<EgoResponse> egos
) {
    public static BannerResponse from(Banner b) {
        return new BannerResponse(
                b.getId(), b.getName(), b.getThumbnailUrl(), b.isActive(),
                b.getIdentities().stream()
                        .sorted(Comparator.comparing(Identity::getRarity).thenComparing(Identity::getName))
                        .map(IdentityResponse::from)
                        .toList(),
                b.getEgos().stream().map(EgoResponse::from).toList()
        );
    }
}