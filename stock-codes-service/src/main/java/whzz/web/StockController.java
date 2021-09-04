package whzz.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import whzz.config.IpConfiguration;
import whzz.pojo.Stock;
import whzz.service.StockService;

import java.util.List;

@RestController
public class StockController {
    @Autowired
    StockService stockService;
    @Autowired
    IpConfiguration ipConfiguration;
    @GetMapping("/codes")
    @CrossOrigin
    public List<Stock> codes() throws Exception {
        System.out.println("current instance's port is "+ ipConfiguration.getPort());
        return stockService.get();
    }
}
