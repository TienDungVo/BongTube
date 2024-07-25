package com.example.bongtube.dao;

import com.example.bongtube.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistDAO extends JpaRepository<Playlist, Integer> {
}
