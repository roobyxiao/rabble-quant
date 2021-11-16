package com.whzz.pojo;

import lombok.Data;
import java.io.Serializable;
import java.sql.Date;

@Data
public class DailyId implements Serializable
{
    private String code;

    private Date date;
}
