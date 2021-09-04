package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import whzz.pojo.StockData;

import java.util.List;

@FeignClient(value = "STOCK-DATA-SERVICE",fallback = whzz.client.StockDataClientFeignHystrix.class)
public interface StockDataClient {
    @GetMapping("/data/{code}")
    public List<StockData> getStockData(@PathVariable("code") String code);
}
