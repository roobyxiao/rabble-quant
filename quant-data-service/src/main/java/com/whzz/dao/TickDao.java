package com.whzz.dao;

import com.whzz.pojo.DailyId;
import com.whzz.pojo.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TickDao extends JpaRepository<Tick, DailyId> {
}
