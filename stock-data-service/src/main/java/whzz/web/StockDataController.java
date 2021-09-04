package whzz.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import whzz.config.IpConfiguration;
import whzz.pojo.StockData;
import whzz.service.StockDataService;

import java.util.List;

@RestController
public class StockDataController {
    @Autowired
    StockDataService stockDataService;
    @Autowired
    IpConfiguration ipConfiguration;

    @GetMapping("/data/{code}")
    public List<StockData> get(@PathVariable("code")String code){
        System.out.println("current instance is :" + ipConfiguration.getPort());
        return stockDataService.get(code);
    }
}
