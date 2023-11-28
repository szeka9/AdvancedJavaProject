package org.ppke.itk.cnctoolpathsharing.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ppke.itk.cnctoolpathsharing.domain.User;

import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequestDto {

    private String name;
    private String emailAddress;
    private String password;

    public static User fromUserCreationRequestDto(UserCreationRequestDto request) {
        var newUser = new User();
        newUser.setRole("ROLE_USER");
        newUser.setName(request.getName());
        newUser.setEmailAddress(request.getEmailAddress());
        newUser.setPassword(request.getPassword().getBytes());
        return newUser;
    }
}
