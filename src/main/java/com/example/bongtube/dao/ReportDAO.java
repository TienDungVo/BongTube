package com.example.bongtube.dao;

import com.example.bongtube.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportDAO extends JpaRepository<Report, Integer> {
}
