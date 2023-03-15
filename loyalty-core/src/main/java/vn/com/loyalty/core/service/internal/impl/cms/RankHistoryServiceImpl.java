package vn.com.loyalty.core.service.internal.impl.cms;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.entity.cms.RankHistoryEntity;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.repository.RankHistoryRepository;
import vn.com.loyalty.core.repository.specification.RankHistorySpecs;
import vn.com.loyalty.core.service.internal.RankHistoryService;

@Service
@RequiredArgsConstructor
public class RankHistoryServiceImpl implements RankHistoryService {

    private final RankHistoryRepository rankHistoryRepository;

    @Override
    public RankHistoryEntity getLastUpdated(String customerCode) {

        return rankHistoryRepository.findFirstBy(RankHistorySpecs.lastUpdated(customerCode), RankHistorySpecs.orderByUpdatedDESC())
                .orElse(rankHistoryRepository.save(
                        RankHistoryEntity.builder()
                                .customerCode(customerCode)
                                .build()
                ));

    }

    @Override
    public void saveHistory(RankHistoryEntity rankHistory) {
        rankHistoryRepository.save(rankHistory);
    }

}
