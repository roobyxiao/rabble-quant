package com.whzz.dao;

import com.whzz.pojo.Daily;
import com.whzz.pojo.DailyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Repository
public interface DailyDao extends JpaRepository<Daily, DailyId>
{
    @Transactional//事务的注解
    @Modifying//增删改必须有这个注解
    @Query(value = "update daily set high_limit =:highLimit, low_limit =:lowLimit  where code = :code and date =:date", nativeQuery = true)
    void updateDaiyLimits(float highLimit, float lowLimit, String code, Date date);
}
