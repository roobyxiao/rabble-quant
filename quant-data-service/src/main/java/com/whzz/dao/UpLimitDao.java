package com.whzz.dao;

import com.whzz.pojo.DailyId;
import com.whzz.pojo.UpLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface UpLimitDao extends JpaRepository<UpLimit, DailyId>
{
    List<UpLimit> findUpLimitsByDate (Date date);
}
