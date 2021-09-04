package whzz.service;

import cn.hutool.core.collection.CollUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import whzz.pojo.StockData;

import java.util.List;

@Service
@CacheConfig(cacheNames = "index_datas")
public class StockDataService {
    @Cacheable(key = "'indexData-code-'+#p0")
    public List<StockData> get(String code){
        return CollUtil.toList();
    }
}
