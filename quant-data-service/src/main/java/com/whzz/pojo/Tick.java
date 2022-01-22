package com.whzz.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Data
public class Tick{

    private String code;

    private Date date;

    private List<TickData> data;

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class TickData implements Serializable {
    private Time t;
    private float p;
    private long v;
    private int bs;
}
