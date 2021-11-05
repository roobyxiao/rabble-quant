package com.whzz.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Data
@Table(name = "calendar")
public class TradeCal {

    @Id
    private Date date;

    private boolean open;
}
