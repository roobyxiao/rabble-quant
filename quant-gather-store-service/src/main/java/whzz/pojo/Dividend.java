package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Dividend implements RowMapper<Dividend> {
    private String code;
    private Date planDate;
    private Date dividendDate;
    private BigDecimal ratio;

    public Dividend() {
    }

    public Dividend(String code, Date planDate, Date dividendDate, BigDecimal ratio) {
        this.code = code;
        this.planDate = planDate;
        this.dividendDate = dividendDate;
        this.ratio = ratio;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public Date getDividendDate() {
        return dividendDate;
    }

    public void setDividendDate(Date dividendDate) {
        this.dividendDate = dividendDate;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    @Override
    public Dividend mapRow(ResultSet resultSet, int i) throws SQLException {
        Dividend dividend = new Dividend();
        dividend.setCode(resultSet.getString("code"));
        dividend.setPlanDate(resultSet.getDate("plan_date"));
        dividend.setDividendDate(resultSet.getDate("dividend_date"));
        dividend.setRatio(resultSet.getBigDecimal("ratio"));
        return dividend;
    }
}
