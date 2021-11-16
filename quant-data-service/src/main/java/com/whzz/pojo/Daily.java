package com.whzz.pojo;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Date;

@Entity
@IdClass(DailyId.class)
@Data
@Table(name = "daily")
public class Daily
{
    @Id
    private String code;

    @Id
    private Date date;

    private float open;

    private float high;

    private float low;

    private float close;

    private float preClose;

    private long volume;

    private long amount;

    private float turn;

    private boolean tradeStatus;

    private float pctChg;

    @Column(name = "is_st")
    private boolean isST;

    private float highLimit;

    private float lowLimit;
}
