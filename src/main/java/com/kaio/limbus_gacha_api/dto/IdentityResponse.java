package com.kaio.limbus_gacha_api.dto;

import com.kaio.limbus_gacha_api.entity.Identity;
import com.kaio.limbus_gacha_api.enums.Rarity;

public record IdentityResponse(
        Long id, String name, Rarity rarity,
        Long sinnerId, String sinnerName,
        String uptie1ArtUrl, String uptie3ArtUrl,
        String uptieCutscene, String obtainmentVideoUrl
) {
    public static IdentityResponse from(Identity i) {
        return new IdentityResponse(
                i.getId(), i.getName(), i.getRarity(),
                i.getSinner().getId(), i.getSinner().getName(),
                i.getUptie1ArtUrl(), i.getUptie3ArtUrl(),
                i.getUptieCutscene(), i.getObtainmentVideoUrl()
        );
    }
}
