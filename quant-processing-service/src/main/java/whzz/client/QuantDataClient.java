package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import whzz.pojo.Daily;
import whzz.pojo.Stock;
import whzz.pojo.TradeCal;
import whzz.pojo.UpLimit;

import java.sql.Date;
import java.util.List;

@FeignClient(value = "QUANT-DATA-SERVICE", fallback = QuantDataClientFeignHystrix.class)
public interface QuantDataClient {
    @RequestMapping(value = "/stock/saveAll", method = RequestMethod.POST)
    public String saveStocks(@RequestBody List<Stock> stocks);

    @GetMapping("/stock/get/{code}")
    public Stock getStock(@PathVariable("code") String code);

    @GetMapping("/stock/exists/{code}")
    public boolean stockExists(@PathVariable("code") String code);

    @GetMapping("/stock/getAll")
    public List<Stock> getAll();

    @RequestMapping(value = "/cal/saveAll", method = RequestMethod.POST)
    public String saveCals(@RequestBody List<TradeCal> cals);

    @RequestMapping(value = "/cal/getMax", method = RequestMethod.GET)
    public String getCalMaxDate();

    @RequestMapping(value = "/cal/getOpenCals/{date}", method = RequestMethod.GET)
    public @ResponseBody List<TradeCal> getOpenCals(@PathVariable("date") String date);

    @RequestMapping(value = "/daily/get/{code}/{date}", method = RequestMethod.GET)
    public Daily getDaily(@PathVariable("code") String code, @PathVariable("date") Date date);

    @RequestMapping(value = "/daily/saveAll", method = RequestMethod.POST)
    public String saveDailies(@RequestBody List<Daily> dailies);

    @RequestMapping(value = "/limit/saveAll", method = RequestMethod.POST)
    public String saveLimits(@RequestBody List<UpLimit> limits);

    @RequestMapping(value = "/limit/save", method = RequestMethod.POST)
    public String saveLimit(@RequestBody UpLimit limit);

    @RequestMapping(value = "/limit/getAll", method = RequestMethod.GET)
    public List<UpLimit> getLimits();
}
