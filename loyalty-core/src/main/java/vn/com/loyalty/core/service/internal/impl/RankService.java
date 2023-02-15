package vn.com.loyalty.core.service.internal.impl;

import org.springframework.lang.Nullable;
import vn.com.loyalty.core.dto.request.RankRequest;
import vn.com.loyalty.core.dto.response.cms.RankResponse;
import vn.com.loyalty.core.entity.cms.RankEntity;

import java.math.BigDecimal;
import java.util.List;

public interface RankService {
    RankResponse createRank(RankRequest rankRequest);

    RankResponse updateRank(RankRequest rankRequest);

    RankResponse deleteRank(RankRequest rankRequest);

    @Nullable
    @SuppressWarnings("unchecked")
    String getRankByPoint(BigDecimal pointNumber, List<RankResponse> rankList);

    List<RankEntity> sortReversalRankList(List<RankEntity> rankEntityList);
}
