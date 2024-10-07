package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Event;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.EventRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public Event createEvent(String username, Event event) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user has the ROLE_COLLECTIVE
        boolean hasCollectiveRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(ERole.ROLE_COLLECTIVE));

        if (!hasCollectiveRole) {
            throw new RuntimeException("User does not have the COLLECTIVE role. Event cannot be created.");
        }

        event.setUser(user);
        return eventRepository.save(event);
    }

    @Value("${upload.path}")
    private String uploadDir;

    public String saveCoverPhoto(MultipartFile file) throws IOException {
        // Generate a unique file name
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Save the file to the server
        Files.copy(file.getInputStream(), filePath);

        return fileName;
    }




    public List<Event> getEventsByUserId(Long userId) {
        return eventRepository.findByUserId(userId);
    }

    // New method to get all events
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }




    // Update an existing event
    public Event updateEvent(int eventId, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id " + eventId));

        // Update the fields
        existingEvent.setName(updatedEvent.getName());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setCoverPhoto(updatedEvent.getCoverPhoto());
        existingEvent.setNumTickets(updatedEvent.getNumTickets());
        existingEvent.setPrice(updatedEvent.getPrice());

        // Save the updated event
        return eventRepository.save(existingEvent);
    }

    // Delete an event
    public void deleteEvent(int eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
        } else {
            throw new NoSuchElementException("Event not found with id " + eventId);
        }
    }



}
