package com.bezkoder.springjwt.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectiveDTO{
    private String phoneNumber;
    private String collectiveName;
    private String organizerDetails;
    private String websiteOrSocialMediaLinks;
    private String descriptionOfEventsOrganized;
    private String eventSafetyProtocols;
    private String profilePhoto;

}
