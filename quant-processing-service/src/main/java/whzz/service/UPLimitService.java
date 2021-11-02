package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.UPLimit;
import java.sql.Date;
import java.util.List;

@Service
public class UPLimitService
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<UPLimit> getAllUPLimits()
    {
        String sql = "SELECT code, date, keep, last FROM up_limit";
        List<UPLimit> limits = jdbcTemplate.query(sql, new UPLimit());
        return limits;
    }

    public List<UPLimit> getUpLimitsByDate(Date date)
    {
        String sql = "SELECT code, date, keep, last FROM up_limit where date = ?";
        List<UPLimit> limits = jdbcTemplate.query(sql, new UPLimit(), date);
        return limits;
    }

    public UPLimit getUPLimit(String code, Date date)
    {
        String sql = "SELECT code, date, keep, last FROM up_limit where code = ? and date = ?";
        List<UPLimit> limits = jdbcTemplate.query(sql, new UPLimit(), code, date);
        return DataAccessUtils.singleResult(limits);
    }
}
