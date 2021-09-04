package whzz.service;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whzz.client.StockDataClient;
import whzz.pojo.AnnualProfit;
import whzz.pojo.Profit;
import whzz.pojo.StockData;
import whzz.pojo.Trade;

import java.util.*;

@Service
public class BackTestService {
    @Autowired
    StockDataClient stockDataClient;
    public List<StockData> listIndexData(String code){
        List<StockData> result=stockDataClient.getStockData(code);
        Collections.reverse(result);
        for(StockData stockData :result){
            System.out.println(stockData.getDate());
        }
        return result;
    }
    public Map<String,Object> simulate(int ma,float sellRate,float buyRate,float serviceCahrge,List<StockData> stockDatas){
        List<Profit> profits=new ArrayList<>();
        List<Trade> trades = new ArrayList<>();
        float initCash=1000;
        float cash=initCash;
        float share=0;//份额
        float value=0; //价值

        int winCount = 0;
        float totalWinRate = 0;
        float avgWinRate = 0;
        float totalLossRate = 0;
        int lossCount = 0;
        float avgLossRate = 0;

        float init =0;//数据的初始值
        if(!stockDatas.isEmpty()){
            init= stockDatas.get(0).getClosePoint();
        }

        for(int i = 0; i< stockDatas.size(); i++){
            StockData stockData = stockDatas.get(i);
            float closePoint= stockData.getClosePoint();
            float avg=getMA(i,ma, stockDatas);
            float max=getMax(i,ma, stockDatas);

            float increase_rate=closePoint/avg;
            float decrease_rate=closePoint/max;
            if(avg!=0){
                //buy 超过了均线
                if(increase_rate>buyRate){
                    //如果没买
                    if(0==share){
                        share=cash/closePoint;
                        cash=0;

                        Trade trade=new Trade();
                        trade.setBuyDate(stockData.getDate());
                        trade.setBuyClosePoint(stockData.getClosePoint());
                        trade.setSellDate("n/a");
                        trade.setSellClosePoint(0);
                        trades.add(trade);
                    }
                }
                //sell低于卖点
                else if(decrease_rate<sellRate){
                    if(0!=share){
                        cash=closePoint*share*(1-serviceCahrge);
                        share=0;

                        Trade trade=trades.get(trades.size()-1);
                        trade.setSellClosePoint(stockData.getClosePoint());
                        trade.setSellDate(stockData.getDate());
                        float rate=cash/initCash;
                        trade.setRate(rate);

                        if (trade.getSellClosePoint() - trade.getBuyClosePoint() > 0) {
                            totalWinRate+=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            winCount++;
                        }else {
                            totalLossRate+=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            lossCount++;
                        }
                    }
                }
            }
            if(share!=0){
                value=closePoint*share;
            }else {
                value=cash;
            }
            float rate=value/initCash;

            Profit profit=new Profit();
            profit.setDate(stockData.getDate());
            profit.setValue(rate*init);

            profits.add(profit);

        }
        avgWinRate =totalWinRate/winCount;
        avgLossRate=totalLossRate/lossCount;

        List<AnnualProfit> annualProfits = caculateAnnualProfits(stockDatas, profits);

        Map<String,Object> map=new HashMap<>();
        map.put("profits",profits);
        map.put("trades",trades);

        map.put("winCount", winCount);
        map.put("lossCount", lossCount);
        map.put("avgWinRate", avgWinRate);
        map.put("avgLossRate", avgLossRate);
        map.put("annualProfits",annualProfits);
        return map;

    }

    private float getMax(int i, int day, List<StockData> list) {
        int start=i-1-day;
        if(start<0){
            return 0;
        }
        int now=i-1;
        float max=0;
        for(int j=start;j<now;j++){
            StockData bean=list.get(j);
            if(bean.getClosePoint()>max){
                max=bean.getClosePoint();
            }
        }
        return max;
    }

    private float getMA(int i, int ma, List<StockData> list) {
        int start=i-1-ma;
        int now=i-1;

        if(start<0){
            return 0;
        }
        float sum=0;
        float avg=0;
        for(int j=start;j<now;j++){
            StockData bean=list.get(j);
            sum+=bean.getClosePoint();
        }
        avg=sum/ma;
        return avg;
    }
    public float getYear(List<StockData> allStockData){
        float years;
        String sDateStart= allStockData.get(0).getDate();
        String sDateEnd= allStockData.get(allStockData.size()-1).getDate();

        Date dateStart= DateUtil.parse(sDateStart);
        Date dateEnd=DateUtil.parse(sDateEnd);

        long days=DateUtil.between(dateStart,dateEnd, DateUnit.DAY);
        years=days/365f;
        return years;
    }
    private int getYear(String date){
        String strYear= StrUtil.subBefore(date,"-",false);
        return Convert.toInt(strYear);
    }
    //计算某一年的的指数收益
    private float getIndexIncome(int year,List<StockData> stockDatas){
        StockData first=null;
        StockData last=null;
        for (StockData stockData : stockDatas){
            String strDate= stockData.getDate();
            int currentYear=getYear(strDate);
            if(currentYear==year){
                if(null==first){
                    first= stockData;
                }
                last= stockData;
            }
        }
        return (last.getClosePoint()-first.getClosePoint())/first.getClosePoint();
    }
    //计算某一年的趋势投资收益
    private float getTrendIncome(int year, List<Profit> profits) {
        Profit first=null;
        Profit last=null;
        for (Profit profit : profits) {
            String strDate = profit.getDate();
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = profit;
                last = profit;
            }
            if(currentYear > year)
                break;
        }
        return (last.getValue() - first.getValue()) / first.getValue();
    }
    //计算完整时间范围内，每一年的指数投资收益和趋势投资收益
    private List<AnnualProfit> caculateAnnualProfits(List<StockData> stockDatas, List<Profit> profits){
        List<AnnualProfit> result=new ArrayList<>();
        String strStartDate = stockDatas.get(0).getDate();
        String strEndDate = stockDatas.get(stockDatas.size()-1).getDate();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        int startYear = DateUtil.year(startDate);
        int endYear = DateUtil.year(endDate);
        for (int year=startYear;year<=endYear;year++){
            AnnualProfit annualProfit=new AnnualProfit();
            annualProfit.setYear(year);
            float indexIncome=getIndexIncome(year, stockDatas);
            float trendIncome=getTrendIncome(year,profits);
            annualProfit.setIndexIncome(indexIncome);
            annualProfit.setTrendIncome(trendIncome);
            result.add(annualProfit);
        }
        return result;
    }

}
