package com.bezkoder.springjwt.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RaverDTO {
    private String fullName;
    private String phoneNumber;
    private String dateOfBirth;
    private String profilePhoto;
    private String governmentId;
    private String socialMediaLinks;
    private String musicPreferences;
}
