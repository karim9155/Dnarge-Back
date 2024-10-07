package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Event;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.EventRepository;
import com.bezkoder.springjwt.services.EventService;
import com.bezkoder.springjwt.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

//    @PostMapping("/create")
//    @PreAuthorize("hasRole('COLLECTIVE')")
//    public Event createEvent(@RequestBody Event event, @RequestParam String username) {
//        return eventService.createEvent(username, event);
//    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('COLLECTIVE') or hasRole('ADMIN')")
    public ResponseEntity<Event> createEvent(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
            @RequestParam("price") Double price,
            @RequestParam("coverPhoto") MultipartFile coverPhoto,
            @RequestParam("numTickets") Integer numTickets,
            @RequestParam("userId") Long userId) {

        try {
            // Save the cover photo and get the filename
            String fileName = eventService.saveCoverPhoto(coverPhoto);

            // Create a new event
            Event event = new Event();
            event.setName(name);
            event.setDescription(description);
            event.setLocation(location);
            event.setDate(date);
            event.setPrice(price);
            event.setCoverPhoto(fileName);
            event.setNumTickets(numTickets);
            // Assuming you have a method to find a user by ID
            User user = userService.getUserById(userId);
            event.setUser(user);

            // Save the event to the database
            Event savedEvent = eventRepository.save(event);

            return ResponseEntity.ok(savedEvent);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('COLLECTIVE') or hasRole('ADMIN')")
    public List<Event> getEventsByUserId(@PathVariable Long userId) {
        return eventService.getEventsByUserId(userId);
    }

    // New endpoint to get all events
    @GetMapping("/all")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('COLLECTIVE') or hasRole('ADMIN')")
    public ResponseEntity<Event> updateEvent(@PathVariable int id,
                                             @RequestParam("name") String name,
                                             @RequestParam("description") String description,
                                             @RequestParam("location") String location,
                                             @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                             @RequestParam("price") double price,
                                             @RequestParam("numTickets") int numTickets,
                                             @RequestParam(value = "coverPhoto", required = false) MultipartFile coverPhoto) throws IOException {
        Optional<Event> eventOptional = eventRepository.findById(id);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            event.setName(name);
            event.setDescription(description);
            event.setLocation(location);
            event.setDate(date);
            event.setPrice(price);
            event.setNumTickets(numTickets);

            if (coverPhoto != null && !coverPhoto.isEmpty()) {
                // Save the new photo to the specified directory
                String fileName = StringUtils.cleanPath(coverPhoto.getOriginalFilename());
                Path uploadDir = Paths.get(uploadPath);

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                try (InputStream inputStream = coverPhoto.getInputStream()) {
                    Path filePath = uploadDir.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IOException("Could not save image file: " + fileName, e);
                }

                // Update the event's cover photo path in the database
                event.setCoverPhoto(fileName);
            }

            Event updatedEvent = eventRepository.save(event);
            return ResponseEntity.ok(updatedEvent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('COLLECTIVE') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable int id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
