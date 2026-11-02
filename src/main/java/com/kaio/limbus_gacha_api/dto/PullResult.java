package com.kaio.limbus_gacha_api.dto;

public record PullResult(
        int position,
        String rarity,
        boolean isNew,
        boolean guaranteed,
        IdentityResponse identity,
        EgoResponse ego
) {}