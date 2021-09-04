package whzz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import whzz.config.OkHttpUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
public class QuantSpiderService {

    @Autowired
    private OkHttpUtil okHttpUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void restoreDividend(String date) throws ParseException {
        String url = "https://datacenter-web.eastmoney.com/api/data/v1/get";
        HashMap params = new HashMap();
        params.put("sortColumns", "SECURITY_CODE");
        params.put("sortTypes", 1);
        params.put("pageSize", "500");
        params.put("reportName", "RPT_SHAREBONUS_DET");
        params.put("columns", "ALL");
        params.put("quoteColumns", "");
        params.put("filter", "(REPORT_DATE%3D%27"+date+"%27)");
        for(int i = 1; i <= 100; i++) {
            params.put("pageNumber", i);
            JSONObject response = JSON.parseObject(okHttpUtil.doGet(url, params));
            JSONObject result = response.getJSONObject("result");
            if (result != null) {
                JSONArray datas = result.getJSONArray("data");
                for(int j = 0; j <datas.size(); j++) {
                    JSONObject data = datas.getJSONObject(j);
                    String code = data.getString("SECUCODE").toLowerCase();
                    Date planDate = data.getDate("PLAN_NOTICE_DATE");
                    Date dividendDate = data.getDate("EX_DIVIDEND_DATE");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = dateFormat.parse("2019-01-01");
                    if(dividendDate != null && dividendDate.before(startDate))
                        continue;
                    Float ratio = data.getFloat("BONUS_IT_RATIO");
                    String querySql = "SELECT COUNT(*) FROM dividend WHERE code = ? AND plan_date = ?";
                    int count = jdbcTemplate.queryForObject(querySql, Integer.class, code, dateFormat.format(planDate));
                    if (count == 0) {
                        String insertSql = "INSERT INTO dividend (code, plan_date, dividend_date, ratio) VALUES (?, ?, ?, ?)";
                        jdbcTemplate.update(insertSql, code, planDate, dividendDate, ratio);
                    } else {
                        String updateSql = "UPDATE dividend SET dividend_date = ?, ratio = ? WHERE code = ? AND plan_date = ? ";
                        jdbcTemplate.update(updateSql, dividendDate, ratio, code, dateFormat.format(planDate));
                    }
                }
            } else {
                break;
            }

        }

    }
}
