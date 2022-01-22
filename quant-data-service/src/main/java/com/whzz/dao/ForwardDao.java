package com.whzz.dao;

import com.whzz.pojo.DailyId;
import com.whzz.pojo.Forward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForwardDao extends JpaRepository<Forward, DailyId> {
    List<Forward> findByCodeOrderByDateDesc(String code);
}
