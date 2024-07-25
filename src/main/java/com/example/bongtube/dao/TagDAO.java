package com.example.bongtube.dao;

import com.example.bongtube.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagDAO extends JpaRepository<Tag, Long> {
}
