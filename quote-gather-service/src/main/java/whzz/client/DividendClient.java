package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import whzz.pojo.Dividend;

import java.util.List;

@FeignClient(value = "QUANT-DATA-SERVICE", contextId = "dividend")
public interface DividendClient {
    @RequestMapping(value = "/dividend/saveAll", method = RequestMethod.POST)
    String saveDividends(@RequestBody List<Dividend> dividends);

    @RequestMapping(value = "/dividend/save", method = RequestMethod.POST)
    String saveDividend(@RequestBody Dividend dividend);

    @GetMapping(value = "/dividend/getCodes")
    List<String> getDistinctCodes();

    @GetMapping(value = "/dividend/get/{code}")
    List<Dividend> getDividendsByCode(@PathVariable("code") String code);
}
