package vn.com.loyalty.core;


import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import vn.com.loyalty.core.repository.MasterDataRepository;

@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration
public class CachingIntegrationTest {

    private RedisServer redisServer;

    @MockBean
    RedisConnectionFactory redisConnectionFactory;
    @MockBean
    MasterDataRepository masterDataRepository;

    @Autowired
    CacheManager cacheManager;



//    @Test
//    void givenRedisCaching_whenFindItemByKey_thenReturnedFromCache() {
//
//        MasterDataEntity data = MasterDataEntity.builder()
//                .key(Constants.MasterDataKey.EPOINT_EXPIRE_TIME)
//                .value("6")
//                .build();
//
//        Mockito.when(masterDataRepository.findByKey(Constants.MasterDataKey.EPOINT_EXPIRE_TIME))
//                .thenReturn(data);
//
//        assert itemFromCache().equals(masterDataRepository.findByKey(Constants.MasterDataKey.EPOINT_EXPIRE_TIME));
//    }
//
//    private Object itemFromCache() {
//        return cacheManager.getCache("masterData");
//    }
}
