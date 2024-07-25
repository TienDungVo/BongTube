package com.example.bongtube.dao.admin;

import com.example.bongtube.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDAO extends JpaRepository<Admin, Integer> {
}
