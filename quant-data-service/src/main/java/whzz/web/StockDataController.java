package whzz.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import whzz.config.IpConfiguration;
import whzz.service.QuantAnalyzeService;

import java.text.ParseException;

@RestController
public class StockDataController {
    @Autowired
    QuantAnalyzeService quantAnalyzeService;
    @Autowired
    IpConfiguration ipConfiguration;

    @GetMapping("/data/limit")
    public void get() throws ParseException {
        System.out.println("current instance is :" + ipConfiguration.getPort());
        quantAnalyzeService.simulate();
    }
}
