package vn.com.loyalty.core.service.internal.impl.cms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final RankMapper rankMapper;
    private final RankRepository rankRepository;
    private final RedisOperation redisOperation;

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
    @Transactional
    public List<RankResponse> syncRankWithRedis() {
        List<RankEntity> rankEntityList = rankRepository.findAll();
        for (RankEntity rankEntity : rankEntityList) {
            redisOperation.setValue(Constants.RedisConstants.RANK_DIR + rankEntity.getRankCode(), rankEntity);
        }
        return rankEntityList.stream().map(rankMapper::entityToDTO).toList();
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
    @Transactional
    public RankResponse deleteRank(RankRequest rankRequest) {

        RankEntity rankEntity = rankRepository.findByRankCode(rankRequest.getRankCode()).orElseThrow(
                () -> new ResourceNotFoundException(RankEntity.class, rankRequest.getRankCode()));

        rankRepository.delete(rankEntity);
        redisOperation.delete(Constants.RedisConstants.RANK_DIR + rankEntity.getRankCode());
        return rankMapper.entityToDTO(rankEntity);
    }

    @Override
    @Transactional
    public RankResponse deleteRank(String rankCode) {

        RankEntity rankEntity = rankRepository.findByRankCode(rankCode).orElseThrow(
                () -> new ResourceNotFoundException(RankEntity.class, rankCode));
        rankRepository.delete(rankEntity);
        redisOperation.delete(Constants.RedisConstants.RANK_DIR + rankEntity.getRankCode());
        return rankMapper.entityToDTO(rankEntity);
    }


    @Override
    public RankEntity getRankByPoint(BigDecimal pointNumber) {

        List<RankEntity> rankList = this.getReversalSortedRankList();
        for (RankEntity rank : rankList) {
            if (pointNumber.compareTo(rank.getRequirePoint()) >= 0) return rank;
        }
        return RankEntity.builder().build();
    }

    @Override
    public List<RankEntity> getReversalSortedRankList() {
        List<RankEntity> rankEntityList = new ArrayList<>();
        try {
            rankEntityList = redisOperation.getValuesMatchPrefix(Constants.RedisConstants.RANK_DIR, RankEntity.class);
            if (CollectionUtils.isEmpty(rankEntityList)) rankEntityList = rankRepository.findAll();
        } catch (Exception e) {
            rankEntityList = rankRepository.findAll();
        } finally {
            rankEntityList.sort((o1, o2) -> o2.getRequirePoint().compareTo(o1.getRequirePoint()));
        }
        return rankEntityList;
    }

    @Override
    public List<RankEntity> getInferiorityRankList(RankEntity currentRank) {
        List<RankEntity> reversalList =  this.getReversalSortedRankList();
        int currentIndex = reversalList.indexOf(currentRank);
        return reversalList.subList(currentIndex, reversalList.size());
    }

    @Override
    public RankEntity getInferiorityRank(RankEntity currentRank) {
        List<RankEntity> reversalList =  this.getReversalSortedRankList();
        int currentIndex = reversalList.indexOf(currentRank);
        return reversalList.get(currentIndex + 1);
    }

    @Override
    public RankEntity getRankByCode(String rankCode) {
        return rankRepository.findByRankCode(rankCode).orElseThrow(() -> new ResourceNotFoundException(RankEntity.class, rankCode));
    }

    @Override
    public List<RankResponse> getRankList(RankRequest data) {
        return this.getReversalSortedRankList().stream().map(rankMapper::entityToDTO).toList();
    }

    @Override
    public RankResponse getRankInform(RankRequest data) {
        return rankMapper.entityToDTO(rankRepository.findByRankCode(data.getRankCode()).orElseThrow(
                () -> new ResourceNotFoundException(RankEntity.class, data.getRankCode())
        ));
    }
}
