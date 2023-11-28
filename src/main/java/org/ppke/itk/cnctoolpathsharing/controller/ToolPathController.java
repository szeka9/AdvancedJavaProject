package org.ppke.itk.cnctoolpathsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppke.itk.cnctoolpathsharing.controller.dto.ToolPathDto;
import org.ppke.itk.cnctoolpathsharing.domain.ToolPath;
import org.ppke.itk.cnctoolpathsharing.domain.User;
import org.ppke.itk.cnctoolpathsharing.repository.ToolPathRepository;
import org.ppke.itk.cnctoolpathsharing.repository.UserRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@RequestMapping("/toolpaths")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ToolPathController {
    private final ToolPathRepository toolPathRepository;
    private final UserRepository userRepository;

    @Operation(summary = "Retrieve a list of tool paths.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of toolpaths."),
            @ApiResponse(responseCode = "400", description = "Invalid url params supplied.")
    })
    @GetMapping(value = "", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public List<ToolPathDto> getToolPaths(
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "created-after", required = false,
                    defaultValue = "#{T(java.time.LocalDateTime).parse(\"1970-01-01T00:00:00\")}") LocalDateTime createdAfter,
            @RequestParam(name = "created-before", required = false,
                    defaultValue = "#{T(java.time.LocalDateTime).now()}") LocalDateTime createdBefore,
            @RequestParam(name = "sort", required = false, defaultValue = "desc") String sort,
            @RequestParam(name = "limit", required = false, defaultValue = "100") Integer limit,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {

        if (! List.of("asc", "desc").contains(sort)) {
            throw new IllegalArgumentException("Invalid sort parameter.");
        }

        var sortParam = sort.equalsIgnoreCase("asc") ?
                Sort.by(Sort.Direction.ASC, "dateOfCreation") :
                Sort.by(Sort.Direction.DESC, "dateOfCreation");

        return toolPathRepository.findByDateOfCreationBetweenAndNameContaining(
                        createdAfter, createdBefore, name, PageRequest.of(page,limit, sortParam))
                .stream().map(ToolPathDto::fromToolPath).toList();
    }

    @Operation(summary = "Retrieve GCode.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "GCode file."),
            @ApiResponse(responseCode = "404", description = "Tool path does not exist.")
    })
    @GetMapping(value = "/{toolpathId}/gcode", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> getGcode(
            Authentication authentication,
            @PathVariable Integer toolpathId
    ) throws IOException {
        Optional<ToolPath> toolpathById = toolPathRepository.findById(toolpathId);

        if (toolpathById.isEmpty()) {
            throw new NoSuchElementException(String.format("Tool path by ID %d does not exist.", toolpathId));
        }

        if (!toolpathById.get().getCreatedByUser().getName().equals(authentication.getName())) {
            throw new AccessDeniedException("Unauthorized.");
        }

        var inputStream = Files.newInputStream(Path.of(toolpathById.get().getFileUri()));
        byte[] binaryData = FileCopyUtils.copyToByteArray(inputStream);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                Path.of(toolpathById.get().getFileUri()).getFileName());

        ByteArrayResource resource = new ByteArrayResource(binaryData);

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.contentLength())
                .contentType(APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "Create new tool path.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tool path is created."),
            @ApiResponse(responseCode = "400", description = "The supplied file is empty."),
            @ApiResponse(responseCode = "409", description = "The file already exists."),
            @ApiResponse(responseCode = "500", description = "IO error occurred on the server.")
    })
    @PostMapping(value = "", consumes = { "multipart/form-data" })
    public void createToolPath(Authentication authentication,
                               @RequestPart("toolPath") ToolPath toolPath,
                               @RequestParam("file") MultipartFile file) throws FileSystemException {

        Optional<User> userById = userRepository.findById(toolPath.getCreatedByUser().getId());

        if (userById.isEmpty()) {
            throw new NoSuchElementException("User does not exist.");
        }

        if (!userById.get().getName().equals(authentication.getName())) {
            throw new AccessDeniedException("Unauthorized.");
        }

        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("The file is empty.");
            }

            var destinationFile = Path.of("upload/" + toolPath.getCreatedByUser().getId() +
                                               "/" + file.getOriginalFilename());
            toolPath.setFileUri(destinationFile.toString());

            if (Files.exists(destinationFile)) {
                throw new FileAlreadyExistsException(String.format("File %s already exists.", file.getOriginalFilename()));
            }

            Files.createDirectories(destinationFile.getParent());
            Files.copy(file.getInputStream(), destinationFile);
        }
        catch (IOException e) {
            String errorMessage = String.format("Failed to store file: %s", file.getOriginalFilename());
            log.error(errorMessage);
            throw new FileSystemException(errorMessage);
        }

        toolPath.setDateOfCreation(LocalDateTime.now());
        toolPathRepository.save(toolPath);
    }

    @Operation(summary = "Delete tool path.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tool path is deleted."),
            @ApiResponse(responseCode = "404", description = "The tool path does not exist.")
    })
    @DeleteMapping(value = "/{toolpathId}")
    public void deleteToolPath(
            Authentication authentication,
            @PathVariable Integer toolpathId
    ) throws IOException {
        Optional<ToolPath> toolpathById = toolPathRepository.findById(toolpathId);

        if (toolpathById.isEmpty()) {
            throw new NoSuchElementException(String.format("Tool path by ID %d does not exist.", toolpathId));
        }

        if (!toolpathById.get().getCreatedByUser().getName().equals(authentication.getName())) {
            throw new AccessDeniedException("Unauthorized.");
        }

        Files.delete(Path.of(toolpathById.get().getFileUri()));
        toolPathRepository.deleteById(toolpathId);
    }
}
