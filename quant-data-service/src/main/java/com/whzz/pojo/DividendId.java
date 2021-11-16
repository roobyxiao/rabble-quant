package com.whzz.pojo;

import lombok.Data;
import java.io.Serializable;
import java.sql.Date;

@Data
public class DividendId implements Serializable
{
    private String code;

    private Date planDate;
}
