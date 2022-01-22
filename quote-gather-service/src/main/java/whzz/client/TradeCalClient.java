package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import whzz.pojo.TradeCal;

import java.util.List;

@FeignClient(value = "QUANT-DATA-SERVICE", contextId = "cal")
public interface TradeCalClient {
    @RequestMapping(value = "/cal/saveAll", method = RequestMethod.POST)
    String saveCals(@RequestBody List<TradeCal> cals);

    @RequestMapping(value = "/cal/getMax", method = RequestMethod.GET)
    String getMaxCalDate();

    @RequestMapping(value = "/cal/getOpenCals/{date}", method = RequestMethod.GET)
    @ResponseBody
    List<TradeCal> getOpenCals(@PathVariable("date") String date);

    @RequestMapping(value = "/cal/isOpen/{date}", method = RequestMethod.GET)
    boolean isOpen(@PathVariable("date") String date);
}
