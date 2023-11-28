package org.ppke.itk.cnctoolpathsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.ppke.itk.cnctoolpathsharing.controller.dto.MachineToolDto;
import org.ppke.itk.cnctoolpathsharing.controller.dto.ToolPathDto;
import org.ppke.itk.cnctoolpathsharing.domain.Machine;
import org.ppke.itk.cnctoolpathsharing.domain.MachineTool;
import org.ppke.itk.cnctoolpathsharing.repository.MachineRepository;
import org.ppke.itk.cnctoolpathsharing.repository.MachineToolRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RequestMapping("/tools")
@RestController
@RequiredArgsConstructor
public class MachineToolController {
    private final MachineToolRepository toolRepository;
    private final MachineRepository machineRepository;

    @Operation(summary = "Get machine tools.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
    })
    @GetMapping(value = "")
    public List<MachineToolDto> getMachineTools(
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "supported-materials", required = false) Set<String> supportedMaterials,
            @RequestParam(name = "supported-by-machines", required = false) Set<Integer> supportedByMachineId) {

        // Note: JPA queries are not implemented for supported materials as it
        // should ideally be a separate relation (not just an individual column of the MachineTool relation).
        // Because of that, filtering takes place here as a workaround.

        return toolRepository.findByNameContaining(name).stream()
            .filter(machineTool ->
                (supportedMaterials == null || machineTool.getSupportedMaterials().containsAll(supportedMaterials)) &&
                (supportedByMachineId == null || machineTool.getSupportedMachines().stream().map(Machine::getId).toList()
                                               .containsAll(supportedByMachineId))
            ).map(MachineToolDto::fromMachineTool).toList();
    }

    @Operation(summary = "Create a machine tool.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Invalid machine ID.")
    })
    @PostMapping(value = "")
    public void createMachineTool(@RequestBody MachineTool tool) {
        for (Machine m : tool.getSupportedMachines()) {
            if (m.getId() == null || !machineRepository.existsById(m.getId())) {
                throw new NoSuchElementException("The referenced machine does not exist.");
            }
        }
        toolRepository.save(tool);
    }

    @Operation(summary = "Delete a tool.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tool is deleted.")
    })
    @DeleteMapping(value = "/{toolId}")
    public void createMachineTool(@PathVariable Integer toolId) {
        toolRepository.deleteById(toolId);
    }
}
