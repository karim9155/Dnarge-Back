package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.MusicTrack;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.MusicTrackRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.MusicTrackService;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tracks")
public class MusicTrackController {

    @Autowired
    private MusicTrackService musicTrackService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicTrackRepository musicTrackRepository;

    public MusicTrackController(UserRepository userRepository, MusicTrackRepository musicTrackRepository, MusicTrackService musicTrackService) {
        this.userRepository = userRepository;
        this.musicTrackRepository = musicTrackRepository;
        this.musicTrackService = musicTrackService;
    }

    @PostMapping("/upload/{userId}")
    @PreAuthorize("hasRole('COLLECTIVE')")
    public ResponseEntity<?> uploadTrack(@PathVariable Long userId,
                                         @RequestParam("file") MultipartFile file,
                                         @RequestParam("trackName") String trackName) {
        try {
            // 1. Validate the track name
            if (trackName == null || trackName.isEmpty()) {
                return ResponseEntity.badRequest().body("Track name is required.");
            }

            // 2. Use Apache Tika to detect the file type based on its content
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());

            // 3. Log the MIME type for debugging
            System.out.println("Detected MIME type: " + mimeType);

            // 4. Validate that the MIME type is an audio format
            if (!mimeType.startsWith("audio/")) {
                return ResponseEntity.badRequest().body("Invalid file format. Only audio files are allowed.");
            }

            // 5. Find the user by ID
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 6. Upload the file and get the URL or file path
            String fileUrl = musicTrackService.uploadTrack(file, trackName);

            // 7. Save the music track in the database
            MusicTrack musicTrack = new MusicTrack(trackName, fileUrl, user);
            musicTrackRepository.save(musicTrack);

            // 8. Return a success response
            return ResponseEntity.ok("Track uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();  // Consider using a logger in production code
            return ResponseEntity.status(500).body("An error occurred while uploading the track: " + e.getMessage());
        }
    }
    @GetMapping("/track/collective/{collectiveId}")
    public ResponseEntity<List<MusicTrack>> getTracksByCollectiveId(@PathVariable Long collectiveId) {
        List<MusicTrack> tracks = musicTrackService.getTracksByCollectiveId(collectiveId);
        if (tracks.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return 204 No Content if no tracks found
        }
        return ResponseEntity.ok(tracks);  // Return the list of tracks
    }
}
