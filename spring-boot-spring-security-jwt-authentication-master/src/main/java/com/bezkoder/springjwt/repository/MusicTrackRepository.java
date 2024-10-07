package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Event;
import com.bezkoder.springjwt.models.MusicTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MusicTrackRepository extends JpaRepository<MusicTrack, Long> {

    @Query(value = "SELECT * FROM music_tracks WHERE user_id = :userId", nativeQuery = true)
    List<MusicTrack> findByUserId(Long userId);

}
