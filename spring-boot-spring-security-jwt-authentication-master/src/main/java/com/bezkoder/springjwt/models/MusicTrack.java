package com.bezkoder.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "music_tracks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MusicTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String trackName;

    @NotBlank
    private String fileUrl; // This will store the file's URL or path

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Constructor without id and uploadDate
    public MusicTrack(String trackName, String fileUrl, User user) {
        this.trackName = trackName;
        this.fileUrl = fileUrl;
        this.user = user;
        this.uploadDate = new Date(); // Automatically set the upload date to the current date
    }
}
