package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import whzz.pojo.Stock;
import whzz.pojo.TradeCal;

import java.util.List;

@FeignClient(value = "QUANT-DATA-SERVICE", fallback = QuantDataClientFeignHystrix.class)
public interface QuantDataClient {
    @RequestMapping(value = "/stock/saveAll", method = RequestMethod.POST)
    public String saveStocks(@RequestBody List<Stock> stocks);
    @GetMapping("/stock/{code}")
    public Stock getStock(@PathVariable("code") String code);
    @GetMapping("/stock/get")
    public List<Stock> getAll();
    @RequestMapping(value = "/cal/saveAll", method = RequestMethod.POST)
    public String saveCals(@RequestBody List<TradeCal> cals);
    @RequestMapping(value = "/cal/max", method = RequestMethod.GET)
    public String getCalMaxDate();

}
