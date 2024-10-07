package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.DTO.CollectiveDTO;
import com.bezkoder.springjwt.DTO.RaverDTO;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.UserRepository;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllCollectiveUsers() {
        return userRepository.findUsersWithCollectiveRole();
    }

    public User getUserById(Long userId) { return userRepository.findById(userId).orElse(null); }

    public User updateRaverDetails(Long userId, RaverDTO raverDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFullName(raverDTO.getFullName());
            user.setPhoneNumber(raverDTO.getPhoneNumber());
            user.setDateOfBirth(raverDTO.getDateOfBirth());
            user.setProfilePhoto(raverDTO.getProfilePhoto());
            user.setGovernmentId(raverDTO.getGovernmentId());
            user.setSocialMediaLinks(raverDTO.getSocialMediaLinks());
            user.setMusicPreferences(raverDTO.getMusicPreferences());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }
    public User updateCollectiveDetails(Long userId, CollectiveDTO collectiveDTO, MultipartFile profilePhoto) throws IOException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPhoneNumber(collectiveDTO.getPhoneNumber());
            user.setCollectiveName(collectiveDTO.getCollectiveName());
            user.setOrganizerDetails(collectiveDTO.getOrganizerDetails());
            user.setWebsiteOrSocialMediaLinks(collectiveDTO.getWebsiteOrSocialMediaLinks());
            user.setDescriptionOfEventsOrganized(collectiveDTO.getDescriptionOfEventsOrganized());
            user.setEventSafetyProtocols(collectiveDTO.getEventSafetyProtocols());

            // Update the profile photo URL if provided
            if (collectiveDTO.getProfilePhoto() != null) {
                user.setProfilePhoto(collectiveDTO.getProfilePhoto());
            }

            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }



    // Method to retrieve Raver details by user ID
    public RaverDTO getRaverDetailsById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return convertToRaverDTO(userOptional.get());
        }
        return null; // Or handle accordingly, like returning an empty DTO
    }

    // Method to retrieve Collective details by user ID
    public CollectiveDTO getCollectiveDetailsById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return convertToCollectiveDTO(userOptional.get());
        }
        return null;
    }

    // Conversion method to map User to CollectiveDTO
    private CollectiveDTO convertToCollectiveDTO(User user) {
        CollectiveDTO collectiveDTO = new CollectiveDTO();
        collectiveDTO.setPhoneNumber(user.getPhoneNumber());
        collectiveDTO.setCollectiveName(user.getCollectiveName());
        collectiveDTO.setOrganizerDetails(user.getOrganizerDetails());
        collectiveDTO.setWebsiteOrSocialMediaLinks(user.getWebsiteOrSocialMediaLinks());
        collectiveDTO.setDescriptionOfEventsOrganized(user.getDescriptionOfEventsOrganized());
        collectiveDTO.setEventSafetyProtocols(user.getEventSafetyProtocols());

        // Assuming you have a 'getProfilePhoto' method or field in your User entity that returns the file path
        collectiveDTO.setProfilePhoto(user.getProfilePhoto());

        return collectiveDTO;
    }

    // Conversion methods to map User entity to RaverDTO
    private RaverDTO convertToRaverDTO(User user) {
        RaverDTO raverDTO = new RaverDTO();
        raverDTO.setFullName(user.getFullName());
        raverDTO.setPhoneNumber(user.getPhoneNumber());
        raverDTO.setDateOfBirth(user.getDateOfBirth());
        raverDTO.setProfilePhoto(user.getProfilePhoto());
        raverDTO.setGovernmentId(user.getGovernmentId());
        raverDTO.setSocialMediaLinks(user.getSocialMediaLinks());
        raverDTO.setMusicPreferences(user.getMusicPreferences());
        return raverDTO;
    }




}
