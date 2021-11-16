package com.whzz.pojo;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Time;

@Entity
@IdClass(DailyId.class)
@Data
@Table(name = "up_limit")
public class UpLimit
{
    @Id
    private String code;

    @Id
    private Date date;

    private Time firstTime;

    private Time endTime;

    private int open;

    private int last;

    private boolean keep;
}
