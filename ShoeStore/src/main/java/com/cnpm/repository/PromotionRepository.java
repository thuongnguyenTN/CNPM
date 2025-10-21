package com.cnpm.repository;

import com.cnpm.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date; // SỬ DỤNG java.util.Date
import java.util.*;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    // Đảm bảo phương thức chấp nhận java.util.Date
    List<Promotion> findByStartDateBeforeAndEndDateAfter(Date startDate, Date endDate);
}