package org.ppke.itk.cnctoolpathsharing.controller.dto;

import lombok.*;
import org.ppke.itk.cnctoolpathsharing.domain.Machine;
import org.ppke.itk.cnctoolpathsharing.domain.MachineType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineDto {

    private Integer id;
    private String name;
    private String manufacturer;
    private Double workspaceWidth;
    private Double workspaceHeight;
    private Double workspaceDepth;
    private String machineType;

    public static MachineDto fromMachine(Machine machine) {
        return new MachineDto(
                machine.getId(),
                machine.getName(),
                machine.getManufacturer(),
                machine.getWorkspaceWidth(),
                machine.getWorkspaceHeight(),
                machine.getWorkspaceDepth(),
                machine.getMachineType().name()
        );
    }
}
