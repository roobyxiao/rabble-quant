package com.whzz.pojo;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@IdClass(DividendId.class)
@Data
@Table(name = "dividend")
public class Dividend
{
    @Id
    private String code;

    @Id
    private Date planDate;

    private Date dividendDate;

    private BigDecimal ratio;
}
