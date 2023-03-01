package vn.com.loyalty.core.service.internal.impl.cms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.dto.request.RankRequest;
import vn.com.loyalty.core.dto.response.cms.RankResponse;
import vn.com.loyalty.core.entity.cms.RankEntity;
import vn.com.loyalty.core.exception.ResourceExistedException;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.mapper.RankMapper;
import vn.com.loyalty.core.repository.RankRepository;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.impl.RankService;
import vn.com.loyalty.core.thirdparty.service.CmsWebClient;
import vn.com.loyalty.core.utils.ObjectUtil;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final RankMapper rankMapper;
    private final RankRepository rankRepository;
    private final RedisOperation redisOperation;
    private final CmsWebClient cmsWebClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RankResponse createRank(RankRequest rankRequest) {

        if (rankRepository.existsByRankCode(rankRequest.getRankCode())) throw new ResourceExistedException(RankEntity.class, rankRequest.getRankCode());
        if (rankRepository.existsByRankName(rankRequest.getRankName())) throw new ResourceExistedException(RankEntity.class, rankRequest.getRankName());

        RankEntity rankEntity = rankRepository.save(rankMapper.DTOToEntity(rankRequest));
        RankResponse rankResponse = rankMapper.entityToDTO(rankEntity);
        redisOperation.setValue(Constants.RedisConstants.RANK_DIR + rankEntity.getRankCode(), rankEntity);
        return rankResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RankResponse updateRank(RankRequest rankRequest) {

        RankEntity rankEntity = rankRepository.findByRankCode(rankRequest.getRankCode()).orElseThrow(
                () -> new ResourceNotFoundException(RankEntity.class, rankRequest.getRankCode()));
        rankEntity = rankRepository.save(ObjectUtil.mergeObject(rankRequest, rankEntity));
        RankResponse rankResponse = rankMapper.entityToDTO(rankEntity);

        redisOperation.setValue(Constants.RedisConstants.RANK_DIR + rankEntity.getRankCode(), rankEntity);

        return rankResponse;
    }

    @Override
    public RankResponse deleteRank(RankRequest rankRequest) {

        RankEntity rankEntity = rankRepository.findByRankCode(rankRequest.getRankCode()).orElseThrow(
                () -> new ResourceNotFoundException(RankEntity.class, rankRequest.getRankCode()));

        rankRepository.delete(rankEntity);
        redisOperation.delete(Constants.RedisConstants.RANK_DIR + rankEntity.getRankCode());
        RankResponse rankResponse = rankMapper.entityToDTO(rankEntity);
        return rankResponse;
    }

    @Override
    @Nullable
    public String getRankByPoint(BigDecimal pointNumber) {

        List<RankResponse> rankList = this.getReversalSortedRankList();
        for (RankResponse rank : rankList) {
            if (pointNumber.compareTo(rank.getRequirePoint()) > 0) return rank.getRankCode();
        }
        return Constants.MasterDataKey.RANK_DEFAULT;
    }

    @Override
    public List<RankResponse> getReversalSortedRankList() {
        List<RankResponse> rankEntityList = new ArrayList<>();
        try {
            rankEntityList = redisOperation.getValuesMatchPrefix(Constants.RedisConstants.RANK_DIR, RankResponse.class);
        } catch (Exception e) {
            rankEntityList = cmsWebClient.receiveRankList().getDataList();
        } finally {
            rankEntityList.sort((o1, o2) -> o2.getRequirePoint().compareTo(o1.getRequirePoint()));
        }
        return rankEntityList;
    }
}
