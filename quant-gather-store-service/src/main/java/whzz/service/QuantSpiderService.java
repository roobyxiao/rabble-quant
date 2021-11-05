package whzz.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import whzz.client.QuantDataClient;
import whzz.config.OkHttpUtil;
import whzz.pojo.Daily;
import whzz.pojo.Dividend;
import whzz.pojo.Stock;
import whzz.pojo.TradeCal;

import java.math.BigDecimal;
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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private QuantDataClient quantDataClient;

    @Autowired
    private TradeCalService tradeCalService;

    @Autowired
    private DailyService dailyService;

    @Autowired
    private DividendService dividendService;

    private static String baseURL = "http://127.0.0.1:5000/";

    public void restoreCalendar()
    {
        String url = baseURL + "calendar";
        Date startDate = Date.valueOf(quantDataClient.getCalMaxDate());
        Date endDate = new Date(DateUtils.addMonths(DateUtil.date(), 1).getTime());
        HashMap params = new HashMap();
        params.put("start_date", startDate.toString());
        params.put("end_date", endDate.toString());
        JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
        int error_code = response.getIntValue("error_code");
        if (error_code == 0) {
            String data = response.getString("data");
            List<TradeCal> cals = JSON.parseArray(data, TradeCal.class);
            quantDataClient.saveCals(cals);
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
                        List<Dividend> dividends = new ArrayList<>();
                        JSONArray datas = result.getJSONArray("data");
                        for (int j = 0; j < datas.size(); j++) {
                            JSONObject data = datas.getJSONObject(j);
                            String[] codes = data.getString("SECUCODE").toLowerCase().split("\\.");
                            String code = codes[1] + "." + codes[0];
                            if(code.startsWith("sh.68"))
                                continue;
                            Date planDate = data.getSqlDate("PLAN_NOTICE_DATE");
                            Date dividendDate = data.getSqlDate("EX_DIVIDEND_DATE");
                            if (dividendDate == null)
                                continue;
                            Date startDate = Date.valueOf("2019-01-01");
                            if (dividendDate.before(startDate))
                                continue;
                            BigDecimal ratio = data.getBigDecimal("BONUS_IT_RATIO");
                            Dividend dividend = new Dividend();
                            dividend.setCode(code);
                            dividend.setPlanDate(planDate);
                            dividend.setDividendDate(dividendDate);
                            dividend.setRatio(ratio);
                            dividends.add(dividend);
                        }
                        dividendService.saveDividends(dividends);
                    } else {
                        break;
                    }
                }
            }
        }
        System.out.println("restore dividend finished");
    }

    public void restoreDaily(Date startDate)
    {
        if(startDate == null)
            startDate = Date.valueOf("2019-01-01");
        String url = baseURL + "daily";
        List<Stock> stocks = quantDataClient.getAll();
        for (Stock stock: stocks){
            System.out.println(stock.getCode());
            HashMap params = new HashMap();
            params.put("code", stock.getCode());
            params.put("start_date", startDate.toString());
            JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                JSONArray datas = response.getJSONArray("data");
                List<Daily> dailies = new ArrayList<>();
                for(int i = 0; i <datas.size(); i++) {
                    Daily daily = new Daily();
                    JSONObject data = datas.getJSONObject(i);
                    daily.setCode(data.getString("code"));
                    daily.setDate(data.getSqlDate("date"));
                    daily.setOpen(data.getFloatValue("open"));
                    daily.setHigh(data.getFloatValue("high"));
                    daily.setLow(data.getFloatValue("low"));
                    daily.setClose(data.getFloatValue("close"));
                    daily.setPreClose(data.getFloatValue("preclose"));
                    daily.setVolume(data.getLongValue("volume"));
                    if (data.getFloat("amount") != null)
                        daily.setAmount(data.getFloat("amount").longValue());
                    daily.setTurn(data.getFloatValue("turn"));
                    daily.setTradeStatus(data.getBooleanValue("tradestatus"));
                    daily.setPctChg(data.getFloatValue("pctChg"));
                    daily.setST(data.getBooleanValue("isST"));
                    dailies.add(daily);
                }
                if (!dailies.isEmpty())
                    dailyService.saveDailies(dailies);
            }
        }
        System.out.println("restore daily finished");
    }

    public void restoreLimit(Date startDate) throws ParseException
    {
        if(startDate == null)
            startDate = Date.valueOf("2019-01-01");
        String url = baseURL + "limit";
        List<TradeCal> tradeCals = tradeCalService.getTradeCals(startDate);
        for (TradeCal cal: tradeCals){
            System.out.println(cal.getDate());
            HashMap params = new HashMap();
            params.put("trade_date", cal.getDate().toString());
            JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                JSONArray datas = response.getJSONArray("data");
                List<Daily> dailies = new ArrayList<>();
                for(int i = 0; i <datas.size(); i++) {
                    JSONObject data = datas.getJSONObject(i);
                    String tsCode = data.getString("ts_code");
                    String[] codes = tsCode.split("\\.");
                    String code = codes[1].toLowerCase() + "." + codes[0];
                    Daily daily = new Daily();
                    daily.setCode(code);
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    String dateStr = data.getString("trade_date");
                    Date date = new Date(dateFormat.parse(dateStr).getTime());
                    daily.setDate(date);
                    float highLimit = data.getFloatValue("up_limit");
                    float lowLimit = data.getFloatValue("down_limit");
                    daily.setHighLimit(highLimit);
                    daily.setLowLimit(lowLimit);
                    dailies.add(daily);
                }
                dailyService.updateDailies(dailies);
            }
        }
        System.out.println("restore limit finish");
    }

    public void restoreEastMoneyLimit(Date date) throws ParseException
    {
        if (date == null)
            date = Date.valueOf("2019-12-02");
        List<TradeCal> tradeCals = tradeCalService.getTradeCals(date);
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
                JSONArray datas = result.getJSONArray("pool");
                System.out.println(datas.size());
                for(int j = 0; j <datas.size(); j++) {
                    JSONObject data = datas.getJSONObject(j);
                    float zdp = data.getFloatValue("zdp");
                    if (zdp > 12 || zdp < 7)
                        continue;
                    String code = data.getString("c");
                    if (code.startsWith("6")) {
                        code = "sh." + code;
                    } else {
                        code = "sz." + code;
                    }
                    Stock stock = quantDataClient.getStock(code);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(stock.getIpoDate());
                    calendar.add(Calendar.YEAR, 1);
                    if(tradeCal.getDate().before(calendar.getTime()))
                        continue;
                    Time first_time = convertTime(data.getString("fbt"));
                    Time end_time = convertTime(data.getString("lbt"));
                    int open = data.getIntValue("zbc");
                    int last = data.getIntValue("lbc");
                    JSONObject zttj = data.getJSONObject("zttj");
                    String statistics = zttj.getString("ct") + "/" + zttj.getString("days");
                    String insertSql = "INSERT INTO up_limit (code, date, first_time, end_time, open, last, statistics) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(insertSql, code, tradeCal.getDate(), first_time, end_time, open, last, statistics);
                }
            }
        }
        System.out.println("restore up_limit finished");
    }

    private Time convertTime(String str)
    {
        if (str.length() == 5)
            str = "0" + str;
        SimpleDateFormat format = new SimpleDateFormat("hhmmss");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Time time = new Time(d.getTime());
        return time;
    }
}
