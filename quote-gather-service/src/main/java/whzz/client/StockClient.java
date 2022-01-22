package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import whzz.pojo.Stock;

import java.util.List;

@FeignClient(value = "QUANT-DATA-SERVICE", contextId = "stock")
public interface StockClient {
    @RequestMapping(value = "/stock/save", method = RequestMethod.POST)
    String saveStock(@RequestBody Stock stock);

    @RequestMapping(value = "/stock/saveAll", method = RequestMethod.POST)
    String saveStocks(@RequestBody List<Stock> stocks);

    @GetMapping(value = "/stock/get/{code}")
    Stock getStock(@PathVariable("code") String code);

    @GetMapping(value = "/stock/exists/{code}")
    boolean stockExists(@PathVariable("code") String code);

    @GetMapping(value = "/stock/getAll")
    List<Stock> getAllStocks();

    @GetMapping(value = "/stock/getListStocks")
    List<Stock> getListedStocks();
}
