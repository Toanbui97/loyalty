package vn.com.loyalty.core.service.internal.impl;

import vn.com.loyalty.core.dto.request.RankRequest;
import vn.com.loyalty.core.dto.response.cms.RankResponse;
import vn.com.loyalty.core.entity.cms.RankEntity;

import java.math.BigDecimal;
import java.util.List;

public interface RankService {
    RankResponse createRank(RankRequest rankRequest);

    RankResponse updateRank(RankRequest rankRequest);

    RankResponse deleteRank(RankRequest rankRequest);

    RankResponse deleteRank(String rankCode);

    RankEntity getRankByPoint(BigDecimal pointNumber);

    List<RankEntity> getReversalSortedRankList();

    RankEntity getInferiorityRank(RankEntity currentRank);

    RankEntity getRankByCode(String rankCode);

    List<RankResponse> getListRank(RankRequest data);

    RankResponse getRankInform(RankRequest data);
}
