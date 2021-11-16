package whzz.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import whzz.client.QuantDataClient;
import whzz.config.OkHttpUtil;
import whzz.pojo.Daily;
import whzz.pojo.DailyLimit;
import whzz.pojo.Dividend;
import whzz.pojo.Stock;
import whzz.pojo.TradeCal;
import whzz.pojo.UpLimit;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.security.cert.TrustAnchor;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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
            stocks.removeIf(stock -> stock.getType() == 2 || stock.getCode().startsWith("sh.68"));
            System.out.println(quantDataClient.saveStocks(stocks));
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
                        dividends.removeIf(dividend -> dividend.getCode().startsWith("sh.68") || dividend.getDividendDate() == null || dividend.getDividendDate().before(Date.valueOf("2019-01-01")));
                        System.out.println(quantDataClient.saveDividens(dividends));
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public void restoreDailyByStock(String startDate)
    {
        if (startDate.isEmpty())
            startDate = "2019-01-01";
        String url = baseURL + "daily";
        List<Stock> stocks = quantDataClient.getAll();
        for (Stock stock: stocks){
            System.out.println(stock.getCode());
            HashMap params = new HashMap();
            params.put("code", stock.getCode());
            params.put("start_date", startDate);
            JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                String data = response.getString("data");
                List<Daily> dailies = JSON.parseArray(data, Daily.class);
                System.out.println(quantDataClient.saveDailies(dailies));
            }
        }
    }

    public void restoreDailyByDate(Date startDate)
    {
        String url = baseURL + "daily";
        List<Daily> dailies = new ArrayList<>();
        List<Stock> stocks = quantDataClient.getAll();
        for (Stock stock: stocks){
            System.out.println(stock.getCode());
            HashMap params = new HashMap();
            params.put("code", stock.getCode());
            params.put("start_date", startDate.toString());
            JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                String data = response.getString("data");
                dailies.addAll(JSON.parseArray(data, Daily.class));

            }
        }
        System.out.println(quantDataClient.saveDailies(dailies));
    }

    public void restoreLimit(String startDate)
    {
        if (startDate.isEmpty())
            startDate = "2019-01-01";
        String url = baseURL + "limit";
        List<TradeCal> tradeCals = quantDataClient.getOpenCals(startDate);
        for (TradeCal cal: tradeCals){
            System.out.println(cal.getDate());
            HashMap params = new HashMap();
            params.put("trade_date", cal.getDate().toString());
            JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                String date = response.getString("data");
                List<DailyLimit> dailies = JSON.parseArray(date, DailyLimit.class);
                System.out.println(quantDataClient.updateDailyLimits(dailies));
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
                limits.removeIf(upLimit -> {
                    boolean remove = upLimit.getZdp() > 12;
                    Stock stock = quantDataClient.getStock(upLimit.getCode());
                    java.util.Date date = DateUtils.addYears(stock.getIpoDate(), 1);
                    if(upLimit.getDate().before(date))
                        remove |= true;
                    return remove;
                });
                System.out.println(quantDataClient.saveLimits(limits));
            }
        }
    }
}
