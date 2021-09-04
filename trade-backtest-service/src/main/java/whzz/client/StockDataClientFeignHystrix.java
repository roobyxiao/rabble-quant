package whzz.client;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.stereotype.Component;
import whzz.pojo.StockData;

import java.util.List;

@Component
public class StockDataClientFeignHystrix implements StockDataClient {
    @Override
    public List<StockData> getStockData(String code) {
        StockData indexData = new StockData();
        indexData.setClosePoint(0);
        indexData.setDate("0000-00-00");
        return CollectionUtil.toList(indexData);
    }
}
