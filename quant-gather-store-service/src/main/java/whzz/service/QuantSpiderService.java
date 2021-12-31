package whzz.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import whzz.client.QuantDataClient;
import whzz.config.OkHttpUtil;
import whzz.pojo.*;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class QuantSpiderService {

    @Autowired
    private OkHttpUtil okHttpUtil;

    @Autowired
    private QuantDataClient quantDataClient;

    private static String baseURL = "http://127.0.0.1:5000/";

    public void restoreCalendar(String startDate)
    {
        String url = baseURL + "calendar";
        Date endDate = new Date(DateUtils.addMonths(DateUtil.date(), 1).getTime());
        HashMap params = new HashMap();
        params.put("start_date", startDate.toString());
        params.put("end_date", endDate.toString());
        JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
        int error_code = response.getIntValue("error_code");
        if (error_code == 0) {
            String data = response.getString("data");
            List<TradeCal> cals = JSON.parseArray(data, TradeCal.class);
            System.out.println(quantDataClient.saveCals(cals));
        }
    }

    public void restoreStock()
    {
        String url = baseURL + "stock";
        HashMap params = new HashMap();
        JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
        int error_code = response.getIntValue("error_code");
        if (error_code == 0) {
            String data = response.getString("data");
            List<Stock> stocks = JSON.parseArray(data, Stock.class);
            stocks.removeIf(stock -> stock.getType() == 2 || stock.getCode().startsWith("sh.68") || (!stock.isStatus()&&stock.getOutDate().before(Date.valueOf("2019-01-01"))));
            System.out.println(quantDataClient.saveStocks(stocks));
        }
        String xqUrl = "https://xueqiu.com/service/v5/stock/preipo/cn/query?order_by=list_date&order=desc&page=1&size=30&type=income";
        JSONObject xqResponse = JSON.parseObject(okHttpUtil.doGet(xqUrl));
        error_code = xqResponse.getIntValue("error_code");
        if (error_code == 0) {
            JSONObject result = xqResponse.getJSONObject("data");
            JSONArray items = result.getJSONArray("items");
            for(int i = 0; i <items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                String symbol = item.getString("symbol");
                String code = symbol.substring(0,2).toLowerCase() + "." + symbol.substring(2);
                if (code.startsWith("sh.68") || code.startsWith("sh.9") || code.startsWith("sz.2"))
                    continue;
                Stock stock = new Stock();
                stock.setCode(code);
                stock.setName(item.getString("name"));
                stock.setIpoDate(new Date(item.getLongValue("list_date")));
                stock.setType(1);
                stock.setStatus(true);
                System.out.println(quantDataClient.saveStock(stock));
            }
        }
        /*String xqUrl = "https://xueqiu.com/service/v5/stock/screener/quote/list?page=1&size=10000&order=asc&order_by=symbol&market=CN&type=sh_sz";
        JSONObject xqResponse = JSON.parseObject(okHttpUtil.doGet(xqUrl));
        error_code = xqResponse.getIntValue("error_code");
        if (error_code == 0) {
            JSONObject result = xqResponse.getJSONObject("data");
            JSONArray datas = result.getJSONArray("list");
            for(int i = 0; i <datas.size(); i++) {
                JSONObject data = datas.getJSONObject(i);
                String symbol = data.getString("symbol");
                String code = symbol.substring(0,2).toLowerCase() + "." + symbol.substring(2);
                if (code.startsWith("sh.68") || code.startsWith("sh.9") || code.startsWith("sz.2"))
                    continue;
                if (!quantDataClient.stockExists(code)) {
                    System.out.println(code + "不存在");
                    System.exit(1);
                }
            }
        }*/
    }

    public void restoreDailyByStock(String startDate)
    {
        if (startDate.isEmpty())
            startDate = "2019-01-01";
        List<Stock> stocks = quantDataClient.getAllStocks();
        for (Stock stock: stocks) {
            restoreDailyByStock(stock.getCode(), startDate);
        }
    }

    private void restoreDailyByStock(String code, String startDate)
    {
        HashMap params = new HashMap();
        params.put("fields1", "f1%2Cf2%2Cf3%2Cf4%2Cf5%2Cf6");
        params.put("fields2", "f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61");
        params.put("klt", "101");
        params.put("fqt", "0");
        params.put("secid", getSecId(code));
        params.put("beg", startDate.replaceAll("-", ""));
        params.put("end", "20500000");
        String xqUrl = "https://push2his.eastmoney.com/api/qt/stock/kline/get";
        JSONObject response = JSON.parseObject(okHttpUtil.doGet(xqUrl, params));
        JSONObject data = response.getJSONObject("data");
        if (data != null) {
            JSONArray datas = data.getJSONArray("klines");
            List<Daily> dailies = new ArrayList<Daily>();
            for(int i = 0; i <datas.size(); i++) {
                String[] items = datas.getString(i).split(",");
                Daily daily = new Daily();
                daily.setCode(code);
                daily.setDate(Date.valueOf(items[0]));
                daily.setVolume(Long.valueOf(items[5]));
                daily.setOpen(Float.valueOf(items[1]));
                daily.setHigh(Float.valueOf(items[3]));
                daily.setLow(Float.valueOf(items[4]));
                daily.setClose(Float.valueOf(items[2]));
                daily.setTurn(Float.valueOf(items[10]));
                float chg = Float.valueOf(items[9]);
                daily.setLastClose(daily.getClose() - chg);
                daily.setAmount(Float.valueOf(items[6]));
                daily.setPercent(Float.valueOf(items[8]));
                dailies.add(daily);
            }
            if (!dailies.isEmpty())
                System.out.println("EastMoney-----" + code + "---" + dailies.size() + "---" + quantDataClient.saveDailies(dailies));
            else
                System.out.println("EastMoney-----" + code + "------ 没有数据");
        }
    }

    public void restoreXqDailyByDate()
    {
        String url = "https://stock.xueqiu.com/v5/stock/quote.json";
        List<XqDaily> dailies = new ArrayList<>();
        List<Stock> stocks = quantDataClient.getAllStocks();
        for (Stock stock: stocks){
            String code = stock.getCode();
            HashMap params = new HashMap();
            String[] codes = code.split("\\.");
            params.put("symbol", codes[0].toUpperCase() + codes[1]);
            params.put("extend", "detail");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                JSONObject data = response.getJSONObject("data");
                String quant = data.getString("quote");
                XqDaily daily = JSON.parseObject(quant, XqDaily.class);
                if (daily.getStatus() == 1 && daily.getOpen() != 0) {
                    System.out.println("xueqiu-----daily------" + code);
                    dailies.add(daily);
                }
            }
        }
        System.out.println("xueqiu-----" + dailies.size() + "---" + quantDataClient.saveXqDailies(dailies));
    }

    public void restoreEmDailyByDate(String startDate)
    {
        String url = "https://push2.eastmoney.com/api/qt/stock/get";
        HashMap params = new HashMap();
        params.put("fields", "f43,f44,f45,f46,f47,f48,f60,f168,f170");
        params.put("fltt", "2");
        List<EmDaily> dailies = new ArrayList<>();
        List<Stock> stocks = quantDataClient.getAllStocks();
        for (Stock stock: stocks){
            String code = stock.getCode();
            params.put("secid", getSecId(code));
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            JSONObject data = response.getJSONObject("data");
            if (data != null) {
                String close = data.getString("f43");
                if (!close.equals("-")) {
                    EmDaily daily = JSON.parseObject(data.toJSONString(), EmDaily.class);
                    daily.setCode(code);
                    daily.setDate(Date.valueOf(startDate));
                    System.out.println("EastMoney-----daily------" + code);
                    dailies.add(daily);
                }
            }
        }
        System.out.println("EastMoney-----" + dailies.size() + "---" + quantDataClient.saveEmDailies(dailies));
    }

    public void restoreLimit(String startDate)
    {
        if (startDate.isEmpty())
            startDate = "2019-01-01";
        String url = baseURL + "limit";
        List<TradeCal> tradeCals = quantDataClient.getOpenCals(startDate);
        for (TradeCal cal: tradeCals){
            HashMap params = new HashMap();
            params.put("trade_date", cal.getDate().toString());
            JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                String date = response.getString("data");
                List<DailyLimit> dailies = JSON.parseArray(date, DailyLimit.class);
                System.out.println(cal.getDate() + "----" + quantDataClient.updateDailyLimits(dailies));
            }
        }
    }

    public void restoreEastMoneyLimit(String startDate)
    {
        if (startDate == null || startDate.isEmpty())
            startDate = "2019-12-02";
        List<TradeCal> tradeCals = quantDataClient.getOpenCals(startDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String url = "https://push2ex.eastmoney.com/getTopicZTPool";
        HashMap params = new HashMap();
        params.put("ut", "7eea3edcaed734bea9cbfc24409ed989");
        params.put("dpt", "wz.ztzt");
        params.put("Pageindex", 0);
        params.put("pagesize", 300);
        params.put("sort", "fbt%3Aasc");
        for(TradeCal tradeCal: tradeCals) {
            System.out.println(tradeCal.getDate());
            params.put("date", dateFormat.format(tradeCal.getDate()));
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            JSONObject result = response.getJSONObject("data");
            if (result != null) {
                String data = result.getString("pool");
                List<UpLimit> limits = JSON.parseArray(data, UpLimit.class);
                limits.forEach(upLimit -> upLimit.setDate(tradeCal.getDate()));
                System.out.println(quantDataClient.saveLimits(limits));
            }
        }
    }

    public void restoreDividend(int startYear) {
        String url = "https://datacenter-web.eastmoney.com/api/data/v1/get";
        HashMap params = new HashMap();
        params.put("sortColumns", "SECURITY_CODE");
        params.put("sortTypes", 1);
        params.put("reportName", "RPT_SHAREBONUS_DET");
        params.put("columns", "ALL");
        params.put("quoteColumns", "");
        int total = 50000;
        String date = "";
        for (int year = startYear; year <= Calendar.getInstance().get(Calendar.YEAR); year++) {
            for (int count = 1; count <= 8; count++) {
                if(count/2 == 1)
                    date = year + "-06-30";
                else
                    date = year + "-12-31";
                int pageSize = count * 10;
                params.put("pageSize", pageSize);
                params.put("filter", "(REPORT_DATE%3D%27" + date + "%27)");
                for (int i = 1; i <= total / pageSize; i++) {
                    params.put("pageNumber", i);
                    JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
                    JSONObject result = response.getJSONObject("result");
                    if (result != null) {
                        String data = result.getString("data");
                        List<Dividend> dividends = JSON.parseArray(data, Dividend.class);
                        dividends.removeIf(dividend -> dividend.getCode().startsWith("bj") ||
                                dividend.getCode().startsWith("sh.68") ||
                                dividend.getDividendDate() == null ||
                                dividend.getDividendDate().before(Date.valueOf("2019-01-01")));
                        System.out.println(quantDataClient.saveDividens(dividends));
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public void restoreTickByDate(String date)
    {
        String url = "https://push2ex.eastmoney.com/getStockFenShi";
        int pageSize = 1000;
        HashMap params = new HashMap();
        params.put("pagesize", pageSize);
        params.put("ut", "7eea3edcaed734bea9cbfc24409ed989");
        params.put("dpt", "wzfscj");
        params.put("sort", "1");
        params.put("ft", "1");
        List<Stock> stocks = quantDataClient.getListStocks();
        for (Stock stock: stocks){
            String code = stock.getCode();
            String[] codes = code.split("\\.");
            params.put("code", codes[1]);
            params.put("id", codes[1] + ("sh".equalsIgnoreCase(codes[0]) ? 1 : 2));
            params.put("market", ("sh".equalsIgnoreCase(codes[0]) ? 1 : 0));
            Tick tick = new Tick(code, Date.valueOf(date), new ArrayList<TickData>());
            for (int i = 0 ; i <= 100 ; i++) {
                params.put("pageindex", i);
                JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
                JSONObject result = response.getJSONObject("data");
                String data = result.getString("data");
                if (result != null) {
                    List<TickData> datas = JSON.parseArray(data, TickData.class);
                    tick.getData().addAll(datas);
                    if (datas.size() < pageSize)
                        break;
                }
            }
            if (!tick.getData().isEmpty())
                System.out.println(stock.getCode() + "-----" + tick.getData().size() + "---" + quantDataClient.saveTick(tick));
            else
                System.out.println(stock.getCode() + "-----没有数据");
        }
        System.out.println("-----打完收工------");
    }

    private String getSecId(String code)
    {
        String[] codes = code.split("\\.");
        return ("sh".equalsIgnoreCase(codes[0]) ? 1 : 0) + "." + codes[1];
    }
}
