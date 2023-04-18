package vn.com.loyalty.core.service.internal.impl.cms;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.entity.cms.RankHistoryEntity;
import vn.com.loyalty.core.repository.RankHistoryRepository;
import vn.com.loyalty.core.repository.specification.RankHistorySpecs;
import vn.com.loyalty.core.service.internal.RankHistoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankHistoryServiceImpl implements RankHistoryService {

    private final RankHistoryRepository rankHistoryRepository;

    @Override
    public RankHistoryEntity getLastUpdated(String customerCode) {

        return rankHistoryRepository.findFirstByCustomerCode(customerCode, RankHistorySpecs.orderByUpdatedDESC())
                .orElse(rankHistoryRepository.save(
                        RankHistoryEntity.builder()
                                .customerCode(customerCode)
                                .build()
                ));

    }

    @Override
    public RankHistoryEntity getForRollback(String customerCode) {
        List<RankHistoryEntity> rankHistoryEntities = rankHistoryRepository.findAll(RankHistorySpecs.orderByUpdatedDESC());
        rankHistoryRepository.delete(rankHistoryEntities.get(0));
        return rankHistoryEntities.get(1);
    }

    @Override
    public void saveHistory(RankHistoryEntity rankHistory) {
        rankHistoryRepository.save(rankHistory);
    }

}
