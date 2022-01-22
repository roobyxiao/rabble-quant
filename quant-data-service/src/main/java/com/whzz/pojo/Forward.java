package com.whzz.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@IdClass(DailyId.class)
@Data
@Table(name = "forward")
public class Forward
{
    @Id
    private String code;

    @Id
    private Date date;

    private float open;

    private float high;

    private float low;

    private float close;

    private float lastClose;

    private long volume;

    private long amount;

    private float turn;

    private float percent;
}
