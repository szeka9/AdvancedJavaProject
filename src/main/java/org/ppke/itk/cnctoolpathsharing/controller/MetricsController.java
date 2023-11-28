package org.ppke.itk.cnctoolpathsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.ppke.itk.cnctoolpathsharing.repository.*;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/metrics")
@RestController
@RequiredArgsConstructor
public class MetricsController {
    private final MachineRepository machineRepository;
    private final MachineToolRepository machineToolRepository;
    private final ToolPathRepository toolPathRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;

    @Operation(summary = "Retrieve basic metrics/statistics.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Map of metrics and statistics.")
    })
    @GetMapping(value = "", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public Map getMetrics() {
        return Map.of("Number of machines", machineRepository.count(),
                      "Number of machine tools", machineToolRepository.count(),
                      "Number of toolpaths", toolPathRepository.count(),
                      "Number of users", userRepository.count(),
                      "Number of groups", userGroupRepository.count());
    }
}
