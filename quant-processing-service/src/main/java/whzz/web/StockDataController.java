package whzz.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import whzz.service.QuantAnalyzeService;

import java.text.ParseException;

@RestController
public class StockDataController {
    @Autowired
    QuantAnalyzeService quantAnalyzeService;

    @GetMapping("/keep")
    public void get() throws ParseException {
        quantAnalyzeService.simulate();
    }
}
