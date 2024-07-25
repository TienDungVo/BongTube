package com.example.bongtube.dao;

import com.example.bongtube.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementDAO extends JpaRepository<Advertisement, Integer> {
}
