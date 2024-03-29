package vn.com.loyalty.core.service.internal;

import vn.com.loyalty.core.entity.cms.RankHistoryEntity;

public interface RankHistoryService {
    RankHistoryEntity getLastUpdated(String customerCode);
    RankHistoryEntity getForRollback(String customerCode);
    void saveHistory(RankHistoryEntity customerEntity);
}
