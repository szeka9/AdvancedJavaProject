package org.ppke.itk.cnctoolpathsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.ppke.itk.cnctoolpathsharing.controller.dto.UserCreationRequestDto;
import org.ppke.itk.cnctoolpathsharing.domain.User;
import org.ppke.itk.cnctoolpathsharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RequestMapping("/signup")
@RestController
@RequiredArgsConstructor
public class SignupController {

    private final UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Operation(summary = "Create a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is created.")
    })
    @PostMapping(value = "")
    public void createUser(@RequestBody UserCreationRequestDto request) {
        User newUser = UserCreationRequestDto.fromUserCreationRequestDto(request);

        newUser.setPassword(passwordEncoder.encode(request.getPassword()).getBytes());
        userRepository.save(newUser);
    }
}
