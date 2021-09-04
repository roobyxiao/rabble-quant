package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Dividend implements RowMapper<Dividend> {
    private String code;
    private Date planDate;
    private Date dividendDate;
    private float ratio;

    public Dividend() {
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

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    @Override
    public Dividend mapRow(ResultSet resultSet, int i) throws SQLException {
        Dividend dividend = new Dividend();
        dividend.setCode(resultSet.getString("code"));
        dividend.setDividendDate(resultSet.getDate("dividend_date"));
        dividend.setRatio(resultSet.getFloat("ratio"));
        return dividend;
    }
}
