package whzz.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import whzz.client.*;
import whzz.config.OkHttpUtil;
import whzz.pojo.*;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class QuoteService {

    @Autowired
    private OkHttpUtil okHttpUtil;

    @Autowired
    private DailyClient dailyClient;
    @Autowired
    private DividendClient dividendClient;
    @Autowired
    private ForwardClient forwardClient;
    @Autowired
    private LimitClient limitClient;
    @Autowired
    private StockClient stockClient;
    @Autowired
    private TickClient tickClient;
    @Autowired
    private TradeCalClient tradeCalClient;

    private static final String baseURL = "http://127.0.0.1:5000/";

    /**
     * 交易日查询
     * baostock query_trade_dates()
     * @param startDate
     */
    public void restoreCalendar(String startDate)
    {
        log.warn("Starting sync calendar······");
        String url = baseURL + "calendar";
        Date endDate = new Date(DateUtils.addMonths(DateUtil.date(), 1).getTime());
        HashMap params = new HashMap();
        params.put("start_date", startDate);
        params.put("end_date", endDate.toString());
        JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
        int error_code = response.getIntValue("error_code");
        if (error_code == 0) {
            String data = response.getString("data");
            List<TradeCal> cals = JSON.parseArray(data, TradeCal.class);
            tradeCalClient.saveCals(cals);
        }
        log.warn("Finish sync calendar······");
    }

    /**
     * 证券基本资料
     * baostock query_stock_basic()
     * baostock缺失数据从雪球 新股行情 获取
     */
    public void restoreStock()
    {
        log.warn("Starting sync stock······");
        String url = baseURL + "stock";
        HashMap params = new HashMap();
        JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
        int error_code = response.getIntValue("error_code");
        if (error_code == 0) {
            String data = response.getString("data");
            List<Stock> stocks = JSON.parseArray(data, Stock.class);
            stocks.removeIf(stock -> stock.getType() != 1 ||
                    !isZhuban(stock.getCode()) ||
                    (!stock.isStatus()&&stock.getOutDate().before(Date.valueOf("2019-01-01"))));
            stockClient.saveStocks(stocks);
        }
        String emUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?sortColumns=LISTING_DATE&sortTypes=-1&pageSize=100&pageNumber=1&reportName=RPTA_APP_IPOAPPLY&columns=SECURITY_CODE%2CSECURITY_NAME%2CTRADE_MARKET_CODE%2CAPPLY_CODE%2CTRADE_MARKET%2CMARKET_TYPE%2CORG_TYPE%2CISSUE_NUM%2CONLINE_ISSUE_NUM%2COFFLINE_PLACING_NUM%2CTOP_APPLY_MARKETCAP%2CPREDICT_ONFUND_UPPER%2CONLINE_APPLY_UPPER%2CPREDICT_ONAPPLY_UPPER%2CISSUE_PRICE%2CLATELY_PRICE%2CCLOSE_PRICE%2CAPPLY_DATE%2CBALLOT_NUM_DATE%2CBALLOT_PAY_DATE%2CLISTING_DATE%2CAFTER_ISSUE_PE%2CONLINE_ISSUE_LWR%2CINITIAL_MULTIPLE%2CINDUSTRY_PE_NEW%2COFFLINE_EP_OBJECT%2CCONTINUOUS_1WORD_NUM%2CTOTAL_CHANGE%2CPROFIT%2CLIMIT_UP_PRICE%2CINFO_CODE%2COPEN_PRICE%2CLD_OPEN_PREMIUM%2CLD_CLOSE_CHANGE%2CTURNOVERRATE%2CLD_HIGH_CHANG%2CLD_AVERAGE_PRICE%2COPEN_DATE%2COPEN_AVERAGE_PRICE%2CPREDICT_PE%2CPREDICT_ISSUE_PRICE2%2CPREDICT_ISSUE_PRICE%2CPREDICT_ISSUE_PRICE1%2CPREDICT_ISSUE_PE%2CPREDICT_PE_THREE%2CONLINE_APPLY_PRICE%2CMAIN_BUSINESS%2CPAGE_PREDICT_PRICE1%2CPAGE_PREDICT_PRICE2%2CPAGE_PREDICT_PRICE3%2CPAGE_PREDICT_PE1%2CPAGE_PREDICT_PE2%2CPAGE_PREDICT_PE3%2CSELECT_LISTING_DATE%2CIS_BEIJING&quoteColumns=f2~01~SECURITY_CODE~NEWEST_PRICE&filter=(APPLY_DATE%3E%272010-01-01%27)";
        JSONObject emResponse = JSON.parseObject(okHttpUtil.doGet(emUrl));
        JSONObject result = emResponse.getJSONObject("result");
        if (result != null) {
            List<Stock> stocks = new ArrayList<>();
            JSONArray items = result.getJSONArray("data");
            for(int i = 0; i <items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                String market = item.getString("MARKET_TYPE");
                String symbol = item.getString("SECURITY_CODE");
                String name = item.getString("SECURITY_NAME");
                String ipoDate = item.getString("LISTING_DATE");
                if (!market.equals("非科创板"))
                    continue;
                String code = symbol.startsWith("6") ? "sh." + symbol : "sz." + symbol;
                if (stockClient.stockExists(code))
                    break;
                Stock stock = new Stock();
                stock.setCode(code);
                stock.setName(name);
                stock.setIpoDate(Date.valueOf(ipoDate.split(" ")[0]));
                stock.setType(1);
                stock.setStatus(true);
                stocks.add(stock);
            }
            stockClient.saveStocks(stocks);
        }
        log.warn("Finish sync stock······");
    }

    /**
     * 校验股票列表
     * 用tushare 股票列表校验本地数据
     */
    public void validateStock()
    {
        log.warn("Starting validate stock······");
        String url = baseURL + "ts_stock";
        HashMap params = new HashMap();
        JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
        int error_code = response.getIntValue("error_code");
        if (error_code == 0) {
            JSONArray datas = response.getJSONArray("data");
            for(int i=0 ; i<datas.size() ; i++) {
                JSONObject data = datas.getJSONObject(i);
                String market = data.getString("market");
                if (!market.equals("主板"))
                    continue;
                String tsCode = data.getString("ts_code").toLowerCase();
                String[] codes = tsCode.split("\\.");
                String code = codes[1] + "." + codes[0];
                if (!stockClient.stockExists(code))
                    log.error(code + "······不存在");
            }
        }
        log.warn("Finish validate stock······");
    }

    /**
     * A股K线数据 (不复权)
     * baostock query_history_k_data_plus()
     * @param startDate
     */
    public void restoreDaily(String startDate)
    {
        log.warn("Starting sync daily······");
        List<Stock> stocks = stockClient.getAllStocks();
        for (Stock stock: stocks) {
            restoreDailyByStock(stock.getCode(), startDate);
        }
        log.warn("Finish sync daily······");
    }

    /**
     * A股K线数据 (前复权)
     * baostock query_history_k_data_plus()
     * @param startDate
     */
    public void restoreForward(String startDate)
    {
        log.warn("Starting sync forward······");
        List<Stock> stocks = stockClient.getAllStocks();
        for (Stock stock: stocks) {
            restoreForwardByStock(stock.getCode(), startDate);
        }
        log.warn("Finish sync forward······");
    }
    /**
     * A股K线数据 (不复权)
     * 东方财富网
     * @param code
     * @param startDate
     */
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
                dailyClient.saveDailies(dailies);
        }
    }

    /**
     * A股K线数据 (前复权)
     * 东方财富网
     * @param code
     * @param startDate
     */
    private void restoreForwardByStock(String code, String startDate)
    {
        HashMap params = new HashMap();
        params.put("fields1", "f1%2Cf2%2Cf3%2Cf4%2Cf5%2Cf6");
        params.put("fields2", "f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61");
        params.put("klt", "101");
        params.put("fqt", "1");
        params.put("secid", getSecId(code));
        params.put("beg", startDate.replaceAll("-", ""));
        params.put("end", "20500000");
        String xqUrl = "https://push2his.eastmoney.com/api/qt/stock/kline/get";
        JSONObject response = JSON.parseObject(okHttpUtil.doGet(xqUrl, params));
        JSONObject data = response.getJSONObject("data");
        if (data != null) {
            JSONArray datas = data.getJSONArray("klines");
            List<Forward> forwards = new ArrayList<>();
            for(int i = 0; i <datas.size(); i++) {
                String[] items = datas.getString(i).split(",");
                Forward forward = new Forward();
                forward.setCode(code);
                forward.setDate(Date.valueOf(items[0]));
                forward.setVolume(Long.valueOf(items[5]));
                forward.setOpen(Float.valueOf(items[1]));
                forward.setHigh(Float.valueOf(items[3]));
                forward.setLow(Float.valueOf(items[4]));
                forward.setClose(Float.valueOf(items[2]));
                forward.setTurn(Float.valueOf(items[10]));
                float chg = Float.valueOf(items[9]);
                forward.setLastClose(forward.getClose() - chg);
                forward.setAmount(Float.valueOf(items[6]));
                forward.setPercent(Float.valueOf(items[8]));
                forwards.add(forward);
            }
            if (!forwards.isEmpty())
                forwardClient.saveForwards(forwards);
        }
    }

    /**
     * A股当日K线
     * 东方财富网
     * @param date
     */
    public int restoreEmDailyByDate(String date)
    {
        log.warn("Starting sync east money daily······");
        String url = "https://push2.eastmoney.com/api/qt/stock/get";
        HashMap params = new HashMap();
        params.put("fields", "f43,f44,f45,f46,f47,f48,f51,f52,f60,f168,f170");
        params.put("fltt", "2");
        List<Daily> dailies = new ArrayList<>();
        List<Stock> stocks = stockClient.getAllStocks();
        for (Stock stock: stocks){
            String code = stock.getCode();
            params.put("secid", getSecId(code));
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            JSONObject data = response.getJSONObject("data");
            if (data != null) {
                String close = data.getString("f43");
                if (!(close.equals("-") || close.equals("0.0"))) {
                    Daily daily = JSON.parseObject(data.toJSONString(), Daily.class);
                    daily.setCode(code);
                    daily.setDate(Date.valueOf(date));
                    dailies.add(daily);
                }
            }
        }
        dailyClient.saveDailies(dailies);
        forwardClient.saveDailies(dailies);
        log.warn("Finish sync east money daily······");
        return dailies.size();
    }

    /**
     * 每日涨跌停价格
     * tushare
     * @param startDate
     */
    public void restoreTushareLimit(String startDate)
    {
        log.warn("Starting sync tushare limit······");
        String url = baseURL + "limit";
        List<TradeCal> tradeCals = tradeCalClient.getOpenCals(startDate);
        for (TradeCal cal: tradeCals){
            HashMap params = new HashMap();
            params.put("trade_date", cal.getDate().toString());
            JSONObject response = JSON.parseObject(okHttpUtil.doPost(url, params));
            int error_code = response.getIntValue("error_code");
            if (error_code == 0) {
                String date = response.getString("data");
                List<TushareLimit> dailies = JSON.parseArray(date, TushareLimit.class);
                dailyClient.updateDailyLimits(dailies);
            }
        }
        log.warn("Finish sync tushare limit······");
    }

    /**
     * 涨停板行情
     * 东方财富网
     * @param startDate
     */
    public void restoreEastMoneyLimit(String startDate)
    {
        log.warn("Starting sync east money limit······");
        List<TradeCal> tradeCals = tradeCalClient.getOpenCals(startDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String url = "https://push2ex.eastmoney.com/getTopicZTPool";
        HashMap params = new HashMap();
        params.put("ut", "7eea3edcaed734bea9cbfc24409ed989");
        params.put("dpt", "wz.ztzt");
        params.put("Pageindex", 0);
        params.put("pagesize", 300);
        params.put("sort", "fbt%3Aasc");
        for(TradeCal tradeCal: tradeCals) {
            log.info(tradeCal.getDate().toString());
            params.put("date", dateFormat.format(tradeCal.getDate()));
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            JSONObject result = response.getJSONObject("data");
            if (result != null) {
                String data = result.getString("pool");
                List<UpLimit> limits = JSON.parseArray(data, UpLimit.class);
                limits.removeIf(upLimit -> !isZhuban(upLimit.getCode()));
                limits.forEach(limit -> {
                    limit.setDate(tradeCal.getDate());
                    Daily daily = dailyClient.getDaily(limit.getCode(), limit.getDate().toString());
                    Stock stock = stockClient.getStock(limit.getCode());
                    if (daily.getClose() != daily.getLimitUp())
                        log.error(limit.getCode() + limit.getDate() + "------不匹配");
                    if (daily.getLow() == daily.getLimitUp())
                        limit.setKeep(true);
                    java.util.Date date = DateUtils.addYears(stock.getIpoDate(), 1);
                    if (limit.getDate().after(date) && limit.getZdp() < 15)
                        limit.setStatus(true);
                });
                limitClient.saveLimits(limits);
            }
        }
        log.warn("Finish sync east money limit······");
    }

    /**
     * 分红送配
     * 东方财富网
     */
    public void restoreDividend(String startDate) {
        log.warn("Starting sync east money dividend······");
        String url = "https://datacenter-web.eastmoney.com/api/data/v1/get";
        HashMap params = new HashMap();
        params.put("sortColumns", "EX_DIVIDEND_DATE");
        params.put("sortTypes", -1);
        params.put("reportName", "RPT_SHAREBONUS_DET");
        params.put("columns", "ALL");
        params.put("quoteColumns", "");
        params.put("pageSize", 500);
        for (int i = 1; i <= 100; i++) {
            params.put("pageNumber", i);
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            JSONObject result = response.getJSONObject("result");
            if (result != null) {
                String data = result.getString("data");
                boolean skipNext = false;
                List<Dividend> list = JSON.parseArray(data, Dividend.class);
                List<Dividend> dividends = new ArrayList<>();
                list.removeIf(dividend -> !isZhuban(dividend.getCode()) ||
                        dividend.getRatio() == null);
                for (Dividend dividend: list) {
                    if (dividend.getDividendDate().before(Date.valueOf(startDate))) {
                        skipNext = true;
                    } else {
                        dividends.add(dividend);
                    }
                }
                if (!dividends.isEmpty())
                    dividendClient.saveDividends(dividends);
                if (skipNext)
                    break;
            }
        }
        log.warn("Finish sync east money dividend······");
    }

    /**
     * 分红送配 当日数据
     * 东方财富网
     * @param date
     */
    public void restoreDividendByDate(String date) {
        log.warn("Starting sync today east money dividend······");
        String url = "https://datacenter-web.eastmoney.com/api/data/v1/get";
        HashMap params = new HashMap();
        params.put("sortColumns", "EX_DIVIDEND_DATE");
        params.put("sortTypes", -1);
        params.put("reportName", "RPT_SHAREBONUS_DET");
        params.put("columns", "ALL");
        params.put("quoteColumns", "");
        params.put("pageSize", 200);
        for (int i = 1; i <= 100; i++) {
            params.put("pageNumber", i);
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            JSONObject result = response.getJSONObject("result");
            if (result != null) {
                String data = result.getString("data");
                boolean skipNext = false;
                List<Dividend> list = JSON.parseArray(data, Dividend.class);
                List<Dividend> dividends = new ArrayList<>();
                list.removeIf(dividend -> !isZhuban(dividend.getCode()) ||
                        dividend.getRatio() == null);
                for (Dividend dividend: list) {
                    if (dividend.getDividendDate().compareTo(Date.valueOf(date)) != 0) {
                        skipNext = true;
                    } else {
                        dividends.add(dividend);
                    }
                }
                if (!dividends.isEmpty())
                    dividendClient.saveDividends(dividends);
                dividends.forEach(dividend -> {
                    forwardClient.deleteByCode(dividend.getCode());
                    restoreForwardByStock(dividend.getCode(), "2019-01-01");
                    updateForward(dividend.getCode());
                });
                if (skipNext)
                    break;
            }
        }
        log.warn("Finish sync today east money dividend······");
    }

    public void updateForwards(){
        log.warn("Starting update forward volume······");
        List<String> codes = dividendClient.getDistinctCodes();
        codes.forEach(code -> updateForward(code));
        log.warn("End update forward volume······");
    }

    private void updateForward(String code) {
        List<Dividend> dividends = dividendClient.getDividendsByCode(code);
        List<Forward> forwards = forwardClient.getForwardsByCode(code);
        float ratio = 1f;
        for (Forward forward: forwards) {
            forward.setVolume( Float.valueOf(forward.getVolume() * ratio).longValue());
            for (Dividend dividend: dividends) {
                if (dividend.getDividendDate().compareTo(forward.getDate()) == 0) {
                    ratio = (dividend.getRatio().floatValue() + 10) / 10;
                }
            }
        }
        forwardClient.saveForwards(forwards);
    }
    /**
     * tick数据
     * 东方财富网
     * @param date
     */
    public int restoreTickByDate(String date)
    {
        log.warn("Starting sync tick······");
        String url = "https://push2ex.eastmoney.com/getStockFenShi";
        int pageSize = 1000;
        HashMap params = new HashMap();
        params.put("pagesize", pageSize);
        params.put("ut", "7eea3edcaed734bea9cbfc24409ed989");
        params.put("dpt", "wzfscj");
        params.put("sort", "1");
        params.put("ft", "1");
        List<Stock> stocks = stockClient.getListedStocks();
        int count = 0;
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
            if (!tick.getData().isEmpty()) {
                count ++;
                tickClient.saveTick(tick);
            }
        }
        log.warn("Finish sync tick······");
        return count;
    }

    private String getSecId(String code)
    {
        String[] codes = code.split("\\.");
        return ("sh".equalsIgnoreCase(codes[0]) ? 1 : 0) + "." + codes[1];
    }

    private boolean isZhuban(String code)
    {
        return !code.startsWith("bj") & !code.startsWith("sh.68");
    }

    public boolean isOpen(String date)
    {
        return tradeCalClient.isOpen(date);
    }
}
