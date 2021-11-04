package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whzz.pojo.Dividend;

import java.util.List;

@Service
public class DividendService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveDividends(List<Dividend> dividends)
    {
        for (Dividend dividend: dividends) {
            String querySql = "SELECT COUNT(1) FROM dividend WHERE code = ? AND plan_date = ?";
            int count = jdbcTemplate.queryForObject(querySql, Integer.class, dividend.getCode(), dividend.getPlanDate());
            if (count == 0) {
                String insertSql = "INSERT INTO dividend (code, plan_date, dividend_date, ratio) VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(insertSql, dividend.getCode(), dividend.getPlanDate(), dividend.getDividendDate(), dividend.getRatio());
            } else {
                String updateSql = "UPDATE dividend SET dividend_date = ?, ratio = ? WHERE code = ? AND plan_date = ? ";
                jdbcTemplate.update(updateSql, dividend.getDividendDate(), dividend.getRatio(), dividend.getCode(), dividend.getPlanDate());
            }
        }
    }
}
