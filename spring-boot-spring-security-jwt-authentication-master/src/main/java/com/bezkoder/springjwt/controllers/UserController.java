package com.bezkoder.springjwt.controllers;
import com.bezkoder.springjwt.DTO.CollectiveDTO;
import com.bezkoder.springjwt.DTO.RaverDTO;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/collectives")
    public List<User> getAllCollectiveUsers() {
        return userService.getAllCollectiveUsers();
    }

    @PutMapping("/raver/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> updateRaverDetails(@PathVariable Long id, @RequestBody RaverDTO raverDTO) {
        User updatedUser = userService.updateRaverDetails(id, raverDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Value("${collective.profile-photo.path}") // Define this in your application.properties
    private String profilePhotoPath;

    @PutMapping("/collective/{id}")
    @PreAuthorize("hasRole('COLLECTIVE')")
    public ResponseEntity<User> updateCollectiveDetails(
            @PathVariable Long id,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("collectiveName") String collectiveName,
            @RequestParam("organizerDetails") String organizerDetails,
            @RequestParam("websiteOrSocialMediaLinks") String websiteOrSocialMediaLinks,
            @RequestParam("descriptionOfEventsOrganized") String descriptionOfEventsOrganized,
            @RequestParam("eventSafetyProtocols") String eventSafetyProtocols,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto) throws IOException {

        // Create the DTO and set all fields
        CollectiveDTO collectiveDTO = new CollectiveDTO();
        collectiveDTO.setPhoneNumber(phoneNumber);
        collectiveDTO.setCollectiveName(collectiveName);
        collectiveDTO.setOrganizerDetails(organizerDetails);
        collectiveDTO.setWebsiteOrSocialMediaLinks(websiteOrSocialMediaLinks);
        collectiveDTO.setDescriptionOfEventsOrganized(descriptionOfEventsOrganized);
        collectiveDTO.setEventSafetyProtocols(eventSafetyProtocols);

        // Handle profile photo if one is uploaded
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            // Save the file to the path specified in application.properties
            String fileName = id + "_" + profilePhoto.getOriginalFilename();
            String filePath = profilePhotoPath + File.separator + fileName;

            // Save the file in the specified directory
            File destinationFile = new File(filePath);
            profilePhoto.transferTo(destinationFile);

            // Set the file URL in the DTO
            String fileUrl = "/uploads/" + fileName; // Assuming you serve static files from /uploads/ path
            collectiveDTO.setProfilePhoto(fileUrl); // Set the relative URL to the image
        } else {
            // If no new file is uploaded, use the existing path (fetch it from the database)
            User existingUser = userService.getUserById(id);  // Add this method to fetch the existing user
            collectiveDTO.setProfilePhoto(existingUser.getProfilePhoto());
        }

        // Call the service to update the user
        User updatedUser = userService.updateCollectiveDetails(id, collectiveDTO, profilePhoto);

        // Return the updated user in the response
        return ResponseEntity.ok(updatedUser);
    }



    @GetMapping("/raver-details/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RaverDTO> getRaverDetails(@PathVariable Long id) {
        RaverDTO raverDetails = userService.getRaverDetailsById(id);
        if (raverDetails != null) {
            return ResponseEntity.ok(raverDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/collective-details/{id}")
    @PreAuthorize("hasRole('COLLECTIVE') or hasRole('USER')")
    public ResponseEntity<CollectiveDTO> getCollectiveDetails(@PathVariable Long id) {
        CollectiveDTO collectiveDetails = userService.getCollectiveDetailsById(id);
        if (collectiveDetails != null) {
            return ResponseEntity.ok(collectiveDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
