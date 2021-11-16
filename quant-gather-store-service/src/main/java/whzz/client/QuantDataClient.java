package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import whzz.pojo.Daily;
import whzz.pojo.DailyLimit;
import whzz.pojo.Dividend;
import whzz.pojo.Stock;
import whzz.pojo.TradeCal;
import whzz.pojo.UpLimit;

import java.sql.Date;
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
    @RequestMapping(value = "/cal/open/{date}", method = RequestMethod.GET)
    public @ResponseBody List<TradeCal> getOpenCals(@PathVariable("date") String date);
    @RequestMapping(value = "/daily/saveAll", method = RequestMethod.POST)
    public String saveDailies(@RequestBody List<Daily> dailies);
    @RequestMapping(value = "/daily/updateLimits", method = RequestMethod.POST)
    public String updateDailyLimits(@RequestBody List<DailyLimit> dailies);
    @RequestMapping(value = "/limit/saveAll", method = RequestMethod.POST)
    public String saveLimits(@RequestBody List<UpLimit> limits);
    @RequestMapping(value = "/dividend/saveAll", method = RequestMethod.POST)
    public String saveDividens(@RequestBody List<Dividend> dividends);
}
