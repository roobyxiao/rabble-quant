package com.whzz.pojo;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Entity
@IdClass(DailyId.class)
@Data
@Table(name = "tick")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Tick{

    @Id
    private String code;

    @Id
    private Date date;

    @Type(type = "json")
    @Column(columnDefinition = "json" )
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