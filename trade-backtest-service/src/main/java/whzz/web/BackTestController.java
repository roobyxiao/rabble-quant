package whzz.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import whzz.pojo.AnnualProfit;
import whzz.pojo.Profit;
import whzz.pojo.StockData;
import whzz.pojo.Trade;
import whzz.service.BackTestService;

import java.util.*;

@RestController
public class BackTestController {
    @Autowired
    BackTestService backTestService;
    @GetMapping("/simulate/{code}/{ma}/{buyThreshold}/{sellThreshold}/{serviceCharge}/{startDate}/{endDate}")
    @CrossOrigin
    public Map<String,Object> backTest(@PathVariable("code") String code
            ,@PathVariable("ma") int ma
            ,@PathVariable("buyThreshold") float buyThreshold
            ,@PathVariable("sellThreshold") float sellThreshold
            ,@PathVariable("serviceCharge") float serviceCharge
            ,@PathVariable("startDate") String strStartDate
            ,@PathVariable("endDate") String strEndDate){
        List<StockData> allStockData = backTestService.listIndexData(code);
        //计算出开始日期和结束日期并返回
        String indexStartDate = allStockData.get(0).getDate();
        String indexEndDate= allStockData.get(allStockData.size()-1).getDate();
        //根据开始日期和结束日期获取对应日期范围的数据
        allStockData =filterByDateRange(allStockData,strStartDate,strEndDate);
        float sellRate=sellThreshold;
        float buyRate=buyThreshold;
        Map<String,?> simulateResult=backTestService.simulate(ma,sellRate,buyRate,serviceCharge, allStockData);
        List<Profit> profits= (List<Profit>) simulateResult.get("profits");
        List<Trade> trades= (List<Trade>) simulateResult.get("trades");

        float years=backTestService.getYear(allStockData);
        float indexIncomeTotal=(allStockData.get(allStockData.size()-1).getClosePoint() - allStockData.get(0).getClosePoint()) / allStockData.get(0).getClosePoint();
        float indexIncomeAnnual = (float) Math.pow(1+indexIncomeTotal, 1/years) - 1;//指数的收益
        float trendIncomeTotal=(profits.get(profits.size()-1).getValue()-profits.get(0).getValue())/profits.get(0).getValue();//趋势投资的收益
        float trendIncomeAnnual = (float) Math.pow(1+trendIncomeTotal, 1/years) - 1;

        int winCount = (Integer) simulateResult.get("winCount");
        int lossCount = (Integer) simulateResult.get("lossCount");
        float avgWinRate = (Float) simulateResult.get("avgWinRate");
        float avgLossRate = (Float) simulateResult.get("avgLossRate");

        List<AnnualProfit> annualProfits= (List<AnnualProfit>) simulateResult.get("annualProfits");
        Map<String,Object> result=new HashMap<>();
        result.put("indexDatas", allStockData);
        result.put("indexStartDate", indexStartDate);
        result.put("indexEndDate", indexEndDate);
        result.put("profits", profits);
        result.put("trades", trades);
        result.put("years", years);
        result.put("indexIncomeTotal", indexIncomeTotal);
        result.put("indexIncomeAnnual", indexIncomeAnnual);
        result.put("trendIncomeTotal", trendIncomeTotal);
        result.put("trendIncomeAnnual", trendIncomeAnnual);

        result.put("winCount", winCount);
        result.put("lossCount", lossCount);
        result.put("avgWinRate", avgWinRate);
        result.put("avgLossRate", avgLossRate);

        result.put("annualProfits", annualProfits);
        return result;
    }

    private List<StockData> filterByDateRange(List<StockData> allStockData, String strStartDate, String strEndDate) {
        if(StrUtil.isBlankOrUndefined(strStartDate) || StrUtil.isBlankOrUndefined(strEndDate) )
            return allStockData;
        List<StockData> result = new ArrayList<>();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        for(StockData stockData : allStockData){
            Date date=DateUtil.parse(stockData.getDate());
            if(date.getTime()>=startDate.getTime()&&
                    date.getTime()<=endDate.getTime()){
                result.add(stockData);
            }
        }
        return result;
    }
}
