package com.kaio.limbus_gacha_api.dto;

import java.util.List;

public record ExtractionResponse(
        List<PullResult> results,
        int pity,
        int pullsUntilGuarantee,
        int currency
) {}