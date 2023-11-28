package org.ppke.itk.cnctoolpathsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppke.itk.cnctoolpathsharing.controller.dto.MachineDto;
import org.ppke.itk.cnctoolpathsharing.controller.dto.MachineToolDto;
import org.ppke.itk.cnctoolpathsharing.domain.Machine;
import org.ppke.itk.cnctoolpathsharing.repository.MachineRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@RequestMapping("/machines")
@Slf4j
@RestController
@RequiredArgsConstructor
public class MachineController {
    private final MachineRepository machineRepository;

    @Operation(summary = "Retrieve a list of machines.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of machines retrieved."),
            @ApiResponse(responseCode = "400", description = "Invalid url params supplied.")
    })
    @GetMapping(value = "", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public List<MachineDto> getMachines(
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "manufacturer", required = false, defaultValue = "") String manufacturer,
            @RequestParam(name = "sort", required = false, defaultValue = "desc") String sort,
            @RequestParam(name = "limit", required = false, defaultValue = "100") Integer limit,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page){

        var sortParam = sort.equalsIgnoreCase("asc") ?
                Sort.by(Sort.Direction.ASC, "name") :
                Sort.by(Sort.Direction.DESC, "name");

        return machineRepository.findByNameContainingAndManufacturerContaining(
                name, manufacturer, PageRequest.of(page,limit, sortParam))
                .stream().map(MachineDto::fromMachine).toList();
    }

    @Operation(summary = "Retrieve a picture of the machine.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Picture of the machine."),
            @ApiResponse(responseCode = "404", description = "Invalid url params supplied.")
    })
    @GetMapping(value = "/{machineId}/picture", produces = MimeTypeUtils.APPLICATION_JSON_VALUE )
    public ResponseEntity<Resource> getPicture(@PathVariable Integer machineId) throws IOException {
        Optional<Machine> machineById = machineRepository.findById(machineId);

        if (machineById.isEmpty()) {
            throw new NoSuchElementException(String.format("Machine by ID %d does not exist.", machineId));
        }

        byte[] binaryData = FileCopyUtils.copyToByteArray(
                (new ClassPathResource("images/" + machineById.get().getPictureUri())).getInputStream());

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + machineById.get().getPictureUri());

        ByteArrayResource resource = new ByteArrayResource(binaryData);

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.contentLength())
                .contentType(APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "Retrieve supported tools.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tools."),
            @ApiResponse(responseCode = "404", description = "Invalid url params supplied.")
    })
    @GetMapping(value = "/{machineId}/tools", produces = MimeTypeUtils.APPLICATION_JSON_VALUE )
    public List<MachineToolDto> getSupportedTools(@PathVariable Integer machineId) {
        Optional<Machine> machineById = machineRepository.findById(machineId);

        if (machineById.isEmpty()) {
            throw new NoSuchElementException(String.format("Machine by ID %d does not exist.", machineId));
        }

        return machineById.get().getSupportedTools().stream().map(MachineToolDto::fromMachineTool).toList();
    }

    @Operation(summary = "Create new machine.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created."),
            @ApiResponse(responseCode = "400", description = "Machine already exists.")
    })
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping(value = "")
    public void createMachine(@RequestBody Machine machine) {

        machineRepository.save(machine);
        log.info(String.format("Machine by ID %d was created.", machine.getId()));
    }
}
