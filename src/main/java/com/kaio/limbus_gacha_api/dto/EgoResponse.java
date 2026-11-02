package com.kaio.limbus_gacha_api.dto;

import com.kaio.limbus_gacha_api.entity.Ego;

public record EgoResponse(
        Long id, String name,
        Long sinnerId, String sinnerName,
        String artUrl, String obtainmentVideoUrl
) {
    public static EgoResponse from(Ego i) {
        return new EgoResponse(
                i.getId(), i.getName(),
                i.getSinner().getId(), i.getSinner().getName(),
                i.getArtUrl(), i.getObtainmentVideoUrl()
        );
    }
}