package org.ppke.itk.cnctoolpathsharing.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ppke.itk.cnctoolpathsharing.domain.Machine;
import org.ppke.itk.cnctoolpathsharing.domain.MachineTool;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineToolDto {

    private Integer id;
    private String name;
    private List<Integer> supportedMachineIds;
    private List<String> supportedMaterials;
    private Integer createdByUserId;

    public static MachineToolDto fromMachineTool(MachineTool machineTool) {
        return new MachineToolDto(
                machineTool.getId(),
                machineTool.getName(),
                machineTool.getSupportedMachines().stream().map(Machine::getId).toList(),
                machineTool.getSupportedMaterials().stream().toList(),
                machineTool.getCreatedByUser().getId()
        );
    }
}
