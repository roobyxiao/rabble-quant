package com.whzz.dao;

import com.whzz.pojo.Daily;
import com.whzz.pojo.DailyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface DailyDao extends JpaRepository<Daily, DailyId>
{
    @Modifying//增删改必须有这个注解
    @Query(value = "UPDATE Daily SET limit_up =:limitUp, limit_down =:limitDown WHERE code = :code AND date =:date", nativeQuery = true)
    void updateDaiyLimits(float limitUp, float limitDown, String code, Date date);
}
