package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.MusicTrack;
import com.bezkoder.springjwt.repository.MusicTrackRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicTrackService {

    private final String uploadDirectory = "/uploads/music-tracks/";
    private final MusicTrackRepository musicTrackRepository;

    public String uploadTrack(MultipartFile file, String trackName) throws IOException {
        // Use Tika to detect the actual MIME type based on file content
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());

        // Log the detected MIME type for debugging (optional)
        System.out.println("Detected MIME type: " + mimeType);

        // Validate if the file is an audio file
        if (!mimeType.startsWith("audio/")) {
            throw new IllegalArgumentException("Invalid file format. Only audio files are allowed.");
        }

        // Generate a unique file name based on the current timestamp and original filename
        String fileName = new Date().getTime() + "-" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDirectory + fileName);

        // Ensure the upload directory exists (create it if necessary)
        if (!Files.exists(Paths.get(uploadDirectory))) {
            Files.createDirectories(Paths.get(uploadDirectory));
        }

        // Save the file to the server
        Files.copy(file.getInputStream(), filePath);

        return fileName; // Return the saved file name (or full path if needed)
    }

    public List<MusicTrack> getTracksByCollectiveId(Long collectiveId) {
        return musicTrackRepository.findByUserId(collectiveId);
    }
}
