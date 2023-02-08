package vn.com.loyalty.core.service.internal.impl.cms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
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
import vn.com.loyalty.core.utils.ObjectUtil;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final RankMapper rankMapper;
    private final RankRepository rankRepository;
    private final RedisOperation redisOperation;

    @Override
    public RankResponse createRank(RankRequest rankRequest) {

        if (rankRepository.existsByRankCode(rankRequest.getRankCode())) throw new ResourceExistedException(RankEntity.class, rankRequest.getRankCode());
        if (rankRepository.existsByRankName(rankRequest.getRankName())) throw new ResourceExistedException(RankEntity.class, rankRequest.getRankName());

        RankEntity rankEntity = rankRepository.save(rankMapper.DTOToEntity(rankRequest));
        RankResponse rankResponse = rankMapper.entityToDTO(rankEntity);
        redisOperation.setValue(Constants.RedisConstants.RANK_DIR + rankEntity.getRankCode(), rankResponse);

        return rankResponse;
    }

    @Override
    public RankResponse updateRank(RankRequest rankRequest) {

        RankEntity rankEntity = rankRepository.findByRankCode(rankRequest.getRankCode()).orElseThrow(
                () -> new ResourceNotFoundException(RankEntity.class, rankRequest.getRankCode()));
        rankEntity = rankRepository.save(ObjectUtil.mergeObject(rankRequest, rankEntity));
        RankResponse rankResponse = rankMapper.entityToDTO(rankEntity);
        redisOperation.setValue(Constants.RedisConstants.RANK_DIR + rankEntity.getRankCode(), rankResponse);

        return rankResponse;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public String getRankByPoint(BigDecimal pointNumber, List<RankResponse> rankList) {

        for (RankResponse rank : rankList) {
            if (pointNumber.compareTo(rank.getRequirePoint()) > 0) return rank.getRankCode();
        }
        return null;
    }

}
