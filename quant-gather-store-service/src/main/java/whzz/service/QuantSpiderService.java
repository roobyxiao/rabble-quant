package whzz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import whzz.config.OkHttpUtil;
import whzz.pojo.Stock;
import whzz.pojo.TradeCal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class QuantSpiderService {

    @Autowired
    private OkHttpUtil okHttpUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StockService stockService;

    @Autowired
    private TradeCalService tradeCalService;

    private static String baseURL = "http://127.0.0.1:5000/";

    public void restoreDividend(String date)
    {
        int total = 50000;
        int pageSize = 100;
        String url = "https://datacenter-web.eastmoney.com/api/data/v1/get";
        HashMap params = new HashMap();
        params.put("sortColumns", "SECURITY_CODE");
        params.put("sortTypes", 1);
        params.put("pageSize", pageSize);
        params.put("reportName", "RPT_SHAREBONUS_DET");
        params.put("columns", "ALL");
        params.put("quoteColumns", "");
        params.put("filter", "(REPORT_DATE%3D%27"+date+"%27)");
        for(int i = 1; i <= total/pageSize; i++) {
            params.put("pageNumber", i);
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            JSONObject result = response.getJSONObject("result");
            if (result != null) {
                JSONArray datas = result.getJSONArray("data");
                for(int j = 0; j <datas.size(); j++) {
                    JSONObject data = datas.getJSONObject(j);
                    String code = data.getString("SECUCODE").toLowerCase();
                    Date planDate = data.getSqlDate("PLAN_NOTICE_DATE");
                    Date dividendDate = data.getSqlDate("EX_DIVIDEND_DATE");
                    if(dividendDate == null)
                        continue;
                    Date startDate = Date.valueOf("2018-01-01");
                    if(dividendDate.before(startDate))
                        continue;
                    Float ratio = data.getFloat("BONUS_IT_RATIO");
                    String querySql = "SELECT COUNT(1) FROM dividend WHERE code = ? AND plan_date = ?";
                    int count = jdbcTemplate.queryForObject(querySql, Integer.class, code, planDate);
                    if (count == 0) {
                        String insertSql = "INSERT INTO dividend (code, plan_date, dividend_date, ratio) VALUES (?, ?, ?, ?)";
                        jdbcTemplate.update(insertSql, code, planDate, dividendDate, ratio);
                    } else {
                        String updateSql = "UPDATE dividend SET dividend_date = ?, ratio = ? WHERE code = ? AND plan_date = ? ";
                        jdbcTemplate.update(updateSql, dividendDate, ratio, code, planDate);
                    }
                }
            } else {
                break;
            }
        }
    }

    public void restoreCalendar()
    {
        String url = baseURL + "calendar";
        Date startDate = Date.valueOf("2018-01-01");
        String querySql = "SELECT MAX(date) FROM calendar";
        Date maxDate= jdbcTemplate.queryForObject(querySql, Date.class);
        if (maxDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(maxDate);
            calendar.add(Calendar.DATE, 1);
            startDate = new Date(calendar.getTime().getTime());
        }
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(new java.util.Date());
        endCalendar.add(Calendar.MONTH, 1);
        Date endDate = new Date(endCalendar.getTime().getTime());
        HashMap params = new HashMap();
        params.put("start_date", startDate.toString());
        params.put("end_date", endDate.toString());
        JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
        int error_code = response.getIntValue("error_code");
        if (error_code == 0) {
            JSONArray datas = response.getJSONArray("data");
            for(int i = 0; i <datas.size(); i++) {
                JSONObject data = datas.getJSONObject(i);
                Date date = data.getSqlDate("calendar_date");
                int open = data.getIntValue("is_trading_day");
                String insertSql = "INSERT INTO calendar (date, open) VALUES (?, ?)";
                jdbcTemplate.update(insertSql, date, open);
            }
        }
    }

    public void restoreStock()
    {
        String url = baseURL + "stock";
        HashMap params = new HashMap();
        JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
        int error_code = response.getIntValue("error_code");
        if (error_code == 0) {
            JSONArray datas = response.getJSONArray("data");
            for(int i = 0; i <datas.size(); i++) {
                JSONObject data = datas.getJSONObject(i);
                String code = data.getString("code");
                String name = data.getString("code_name");
                Date ipoDate = data.getSqlDate("ipoDate");
                Date outDate = data.getSqlDate("outDate");
                int type = data.getIntValue("type");
                int status = data.getIntValue("status");
                if(type == 2)
                    continue;
                if(code.startsWith("sh.688"))
                    continue;
                String querySql = "SELECT COUNT(1) FROM stock WHERE code = ?";
                int count = jdbcTemplate.queryForObject(querySql, Integer.class, code);
                if (count == 0) {
                    String insertSql = "INSERT INTO stock (code, name, ipo_date, out_date, status) VALUES (?, ?, ?, ?, ?)";
                    jdbcTemplate.update(insertSql, code, name, ipoDate, outDate, status);
                } else {
                    String updateSql = "UPDATE stock SET name = ?, ipo_date = ?, out_date = ?, status = ?  WHERE code = ?";
                    jdbcTemplate.update(updateSql, name, ipoDate, outDate, status, code);
                }
            }
        }
    }

    public void restoreDaily()
    {
        String url = baseURL + "daily";
        Date startDate = Date.valueOf("2018-01-01");
        String querySql = "SELECT MAX(date) FROM daily";
        Date maxDate = jdbcTemplate.queryForObject(querySql, Date.class);
        if (maxDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(maxDate);
            calendar.add(Calendar.DATE, 1);
            startDate = new Date(calendar.getTime().getTime());
        }
        List<Stock> stocks = stockService.getAllStocks();
        for (Stock stock: stocks){
            System.out.println(stock.getCode());
            HashMap params = new HashMap();
            params.put("code", stock.getCode());
            params.put("start_date", startDate.toString());
            JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                JSONArray datas = response.getJSONArray("data");
                for(int i = 0; i <datas.size(); i++) {
                    JSONObject data = datas.getJSONObject(i);
                    String code = data.getString("code");
                    Date date = data.getSqlDate("date");
                    float open = data.getFloatValue("open");
                    float high = data.getFloatValue("high");
                    float low = data.getFloatValue("low");
                    float close = data.getFloatValue("close");
                    float preClose = data.getFloatValue("preclose");
                    long volume = data.getLongValue("volume");
                    float amount = data.getFloatValue("amount");
                    float turn = data.getFloatValue("turn");
                    int tradeStatus = data.getIntValue("tradestatus");
                    float pctChg = data.getFloatValue("pctChg");
                    int isST = data.getIntValue("isST");
                    String insertSql = "INSERT INTO daily (code, date, open, high, low, close, pre_close, volume, amount, turn, trade_status, pct_chg, is_st) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(insertSql, code, date, open, high, low, close, preClose, volume, amount, turn, tradeStatus, pctChg, isST);
                }
            }
        }
    }

    public void restoreLimit() throws ParseException
    {
        String url = baseURL + "limit";
        Date startDate = Date.valueOf("2021-10-18");
        /*String querySql = "SELECT MAX(date) FROM daily WHERE high_limit IS NOT NULL";
        Date maxDate = jdbcTemplate.queryForObject(querySql, Date.class);
        if (maxDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(maxDate);
            calendar.add(Calendar.DATE, 1);
            startDate = new Date(calendar.getTime().getTime());
        }*/
        List<TradeCal> calendars = tradeCalService.getCalendars(startDate);
        for (TradeCal calendar: calendars){
            System.out.println(calendar.getDate());
            HashMap params = new HashMap();
            params.put("trade_date", calendar.getDate().toString());
            JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                JSONArray datas = response.getJSONArray("data");
                for(int i = 0; i <datas.size(); i++) {
                    JSONObject data = datas.getJSONObject(i);
                    String tsCode = data.getString("ts_code");
                    String[] codes = tsCode.split("\\.");
                    String code = codes[1].toLowerCase() + "." + codes[0];
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    String dateStr = data.getString("trade_date");
                    Date date = new Date(dateFormat.parse(dateStr).getTime());
                    float highLimit = data.getFloatValue("up_limit");
                    float lowLimit = data.getFloatValue("down_limit");
                    String updateSql = "UPDATE daily SET high_limit = ?, low_limit = ? "
                                    + "WHERE code = ? and date = ?";
                    jdbcTemplate.update(updateSql, highLimit, lowLimit, code, date);
                }
            }
        }
    }

    public void restoreUpLimit() throws ParseException
    {
        String url = "https://homeflashdata2.jrj.com.cn/limitStatistic/ztForce/";
        Date startDate = Date.valueOf("2018-01-01");
        List<TradeCal> calendars = tradeCalService.getCalendars(startDate);
        for (TradeCal calendar: calendars){
            System.out.println(calendar.getDate());
            HashMap params = new HashMap();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String response = okHttpUtil.doGet(url + dateFormat.format(calendar.getDate()) + ".js");
            int start = response.indexOf("[");
            int end = response.lastIndexOf("]");
            String jsonResponse = response.substring(start, end+1);
            String replaced = jsonResponse.replaceAll("Infinity", "100");
            JSONArray jsonArray = JSON.parseArray(replaced);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray array = JSON.parseArray(jsonArray.getString(i));
                String code = array.getString(0);
                if(code.startsWith("3"))
                    continue;
                if(code.startsWith("68"))
                    continue;
                if(code.startsWith("0"))
                    code = "sz." + code;
                if(code.startsWith("6"))
                    code = "sh." + code;
                float percentage = array.getFloatValue(3);
                if(percentage > 15 || percentage < 7)
                    continue;
                String timeStr = array.getString(7);
                Time time = null;
                if(!StringUtils.isEmpty(timeStr)) {
                    time = Time.valueOf(timeStr);
                }
                int open = array.getIntValue(9);
                String insertSql = "INSERT INTO up_limit (code, date, time, open) "
                                + "VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(insertSql, code, calendar.getDate(), time, open);
            }
        }
    }

    public void restoreEastMoneyLimit(Date date) throws ParseException
    {
        if (date == null)
            date = Date.valueOf("2019-12-02");
        List<TradeCal> tradeCals = tradeCalService.getCalendars(date);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String url = "https://push2ex.eastmoney.com/getTopicZTPool";
        HashMap params = new HashMap();
        params.put("ut", "7eea3edcaed734bea9cbfc24409ed989");
        params.put("dpt", "wz.ztzt");
        params.put("Pageindex", 0);
        params.put("pagesize", 300);
        params.put("sort", "fbt%3Aasc");
        for(TradeCal cal: tradeCals) {
            params.put("date", dateFormat.format(cal.getDate()));
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            JSONObject result = response.getJSONObject("data");
            if (result != null) {
                JSONArray datas = result.getJSONArray("pool");
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
                    Time first_time = convertTime(data.getString("fbt"));
                    Time end_time = convertTime(data.getString("lbt"));
                    int open = data.getIntValue("zbc");
                    int last = data.getIntValue("lbc");
                    JSONObject zttj = data.getJSONObject("zttj");
                    String statistics = zttj.getString("ct") + "|" + zttj.getString("days");
                    String insertSql = "INSERT INTO up_limit (code, date, first_time, end_time, open, last, statistics) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(insertSql, code, cal.getDate(), first_time, end_time, open, last, statistics);
                }
            }
        }
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
