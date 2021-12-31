package whzz.service;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import whzz.client.QuantDataClient;
import whzz.pojo.Daily;
import whzz.pojo.Stock;
import whzz.pojo.UpLimit;

import java.util.Date;
import java.util.List;

@Component
public class QuantProcessingService {

    @Autowired
    private QuantDataClient quantDataClient;

    public void processUpLimit()
    {
        List<UpLimit> limits = quantDataClient.getLimits();
        limits.forEach(upLimit -> {
            Stock stock = quantDataClient.getStock(upLimit.getCode());
            Daily daily = quantDataClient.getDaily(upLimit.getCode(), upLimit.getDate());
            if (daily.getClose() != daily.getHighLimit())
                System.out.println(upLimit.getCode() + upLimit.getDate() + "------不匹配");
            if (daily.getLow() == daily.getHighLimit())
                upLimit.setKeep(true);
            Date date = DateUtils.addYears(stock.getIpoDate(), 1);
            if (upLimit.getDate().before(date))
                upLimit.setStatus(false);
            if (daily.getPercent() > 15)
                upLimit.setStatus(false);
            if (upLimit.isKeep() || !upLimit.isStatus())
                quantDataClient.saveLimit(upLimit);
        });
    }
}
