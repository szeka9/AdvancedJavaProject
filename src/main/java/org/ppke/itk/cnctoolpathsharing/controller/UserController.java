package org.ppke.itk.cnctoolpathsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.ppke.itk.cnctoolpathsharing.controller.dto.MachineToolDto;
import org.ppke.itk.cnctoolpathsharing.controller.dto.ToolPathDto;
import org.ppke.itk.cnctoolpathsharing.controller.dto.UserDto;
import org.ppke.itk.cnctoolpathsharing.repository.UserRepository;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "Get information about a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information."),
            @ApiResponse(responseCode = "404", description = "The user does not exist.")
    })
    @GetMapping(value = "/{userId}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public UserDto getUser(@PathVariable Integer userId){
        var result = userRepository.findById(userId);

        if (result.isEmpty()) {
            throw new NoSuchElementException(String.format("User by ID %s does not exist.", userId));
        }

        return UserDto.fromUser(result.get());
    }

    @Operation(summary = "Get tool paths of a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tool paths of a user."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @GetMapping(value = "/{userId}/toolpaths", consumes = { "multipart/form-data" })
    public List<ToolPathDto> getToolPaths(@PathVariable Integer userId){
        var foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()) {
            return foundUser.get().getToolPaths().stream().map(ToolPathDto::fromToolPath).toList();
        }
        else {
            throw new NoSuchElementException(String.format("User by ID %s does not exist.", userId));
        }
    }

    @Operation(summary = "Get tools of a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tools of a user."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @GetMapping(value = "/{userId}/tools", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public List<MachineToolDto> getMachineTools(@PathVariable Integer userId){
        var foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()) {
            return foundUser.get().getMachineTools().stream().map(MachineToolDto::fromMachineTool).toList();
        }
        else {
            throw new NoSuchElementException(String.format("User by ID %s does not exist.", userId));
        }
    }
}
