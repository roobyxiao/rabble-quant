package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import whzz.pojo.Daily;
import whzz.pojo.TushareLimit;

import java.util.List;

@FeignClient(value = "QUANT-DATA-SERVICE", contextId = "daily")
public interface DailyClient {
    @GetMapping(value = "/daily/get/{code}/{date}")
    Daily getDaily(@PathVariable("code") String code, @PathVariable("date") String date);

    @RequestMapping(value = "/daily/saveAll", method = RequestMethod.POST)
    String saveDailies(@RequestBody List<Daily> dailies);

    @RequestMapping(value = "/daily/updateDailyLimits", method = RequestMethod.POST)
    String updateDailyLimits(@RequestBody List<TushareLimit> limits);
}
