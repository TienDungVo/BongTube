package com.example.bongtube.dao.admin;

import com.example.bongtube.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLogDAO extends JpaRepository<AdminLog, Integer> {
}
