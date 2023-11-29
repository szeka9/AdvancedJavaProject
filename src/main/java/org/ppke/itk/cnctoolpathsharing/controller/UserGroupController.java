package org.ppke.itk.cnctoolpathsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppke.itk.cnctoolpathsharing.controller.dto.ToolPathDto;
import org.ppke.itk.cnctoolpathsharing.controller.dto.UserDto;
import org.ppke.itk.cnctoolpathsharing.controller.dto.UserGroupDto;
import org.ppke.itk.cnctoolpathsharing.domain.ToolPath;
import org.ppke.itk.cnctoolpathsharing.domain.UserGroup;
import org.ppke.itk.cnctoolpathsharing.repository.UserGroupRepository;
import org.ppke.itk.cnctoolpathsharing.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequestMapping("/groups")
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserGroupController {
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;

    @Operation(summary = "List user groups.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user groups.")
    })
    @GetMapping(value = "", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public List<UserGroupDto> getUserGroup(@RequestParam(name = "name", required = false, defaultValue = "") String name,
                                        @RequestParam(name = "sort", required = false, defaultValue = "desc") String sort,
                                        @RequestParam(name = "limit", required = false, defaultValue = "100") Integer limit,
                                        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page){

        var sortParam = sort.equalsIgnoreCase("asc") ?
                Sort.by(Sort.Direction.ASC, "name") :
                Sort.by(Sort.Direction.DESC, "name");

        return userGroupRepository.findByNameContainingIgnoreCase(name, PageRequest.of(page,limit, sortParam)).
                stream().map(UserGroupDto::fromUserGroup).toList();
    }

    @Operation(summary = "Create a new group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User group is created.")
    })
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createGroup(
            Authentication authentication,
            @RequestBody UserGroup userGroup){

        var foundUser = userRepository.findByName(authentication.getName());
        if (foundUser.isEmpty()) {
            throw new NoSuchElementException(String.format("Invalid user."));
        }
        userGroup.setManagedByUser(foundUser.get());
        userGroupRepository.save(userGroup);
    }

    @Operation(summary = "Add or remove members of a group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group is patched."),
            @ApiResponse(responseCode = "400", description = "Invalid argument is supplied."),
            @ApiResponse(responseCode = "404", description = "Group or user does not exist.")
    })
    @PatchMapping(value = "/{groupId}", consumes = { "multipart/form-data" })
    public void patchGroup(Authentication authentication,
                           @PathVariable Integer groupId,
                           @RequestPart(value = "addUser", required = false) Integer addUser,
                           @RequestPart(value = "removeUser", required = false) Integer removeUser){

        var foundGroup = userGroupRepository.findById(groupId);

        if (foundGroup.isEmpty()) {
            throw new NoSuchElementException(String.format("Group by ID %s does not exist.", groupId));
        }

        if (!Objects.equals(authentication.getName(), foundGroup.get().getManagedByUser().getName())) {
            throw new AccessDeniedException("User is unauthorized to modify the group.");
        }

        if (addUser != null && removeUser == null) {
            var userToAdd = userRepository.findById(addUser);

            if (userToAdd.isEmpty()) {
                throw new NoSuchElementException(String.format("User by ID %s does not exist.", addUser));
            }

            foundGroup.get().addMember(userToAdd.get());
            userGroupRepository.save(foundGroup.get());
        }
        else if (addUser == null && removeUser != null) {
            var userToRemove = userRepository.findById(removeUser);

            if (userToRemove.isEmpty()) {
                throw new NoSuchElementException(String.format("User by ID %s does not exist.", removeUser));
            }

            foundGroup.get().removeMember(userToRemove.get());
            userGroupRepository.save(foundGroup.get());
        }
        else {
            throw new IllegalArgumentException("addUser and removeUser arguments are invalid.");
        }
    }

    @Operation(summary = "Delete a group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group is deleted."),
            @ApiResponse(responseCode = "404", description = "Group does not exist.")
    })
    @DeleteMapping(value = "/{groupId}")
    public void deleteGroup(Authentication authentication,
                            @PathVariable Integer groupId){
        var foundGroup = userGroupRepository.findById(groupId);

        if (foundGroup.isEmpty()) {
            throw new NoSuchElementException(String.format("Group by ID %s does not exist.", groupId));
        }

        if (!Objects.equals(authentication.getName(), foundGroup.get().getManagedByUser().getName())) {
            throw new AccessDeniedException("User is unauthorized to delete the group.");
        }

        // Remove foreign key references from tool paths.
        foundGroup.get().getToolPaths().stream()
                .filter(toolPath -> toolPath.getVisibleByGroup() == foundGroup.get())
                .forEach(toolPath -> toolPath.setVisibleByGroup(null));

        userGroupRepository.deleteById(groupId);
        log.info(String.format("Group by ID %d is deleted.", groupId));
    }

    @Operation(summary = "List members of a group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of members (users)."),
            @ApiResponse(responseCode = "404", description = "Group does not exist.")
    })
    @GetMapping(value = "/{groupId}/members", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public List<UserDto> getUsers(@PathVariable Integer groupId){
        var foundGroup = userGroupRepository.findById(groupId);

        if (foundGroup.isEmpty()) {
            throw new NoSuchElementException(String.format("Group by ID %s does not exist.", groupId));
        }

        return foundGroup.get().getGroupMembers().stream().map(UserDto::fromUser).toList();
    }

    @Operation(summary = "Get tool paths of a group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tool paths."),
            @ApiResponse(responseCode = "404", description = "Group does not exist.")
    })
    @GetMapping(value = "/{groupId}/toolpaths", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public List<ToolPathDto> getToolPaths(Authentication authentication,
                                          @PathVariable Integer groupId){
        var foundGroup = userGroupRepository.findById(groupId);
        var foundUser = userRepository.findByName(authentication.getName());

        if (foundGroup.isEmpty()) {
            throw new NoSuchElementException(String.format("Group by ID %s does not exist.", groupId));
        }

        if (!foundUser.get().getMembership().contains(foundGroup.get()) &&
            !foundUser.get().equals(foundGroup.get().getManagedByUser())) {
            throw new AccessDeniedException("User is unauthorized to list toolpaths.");
        }

        return foundGroup.get().getToolPaths().stream().map(ToolPathDto::fromToolPath).toList();
    }
}
