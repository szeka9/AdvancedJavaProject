package org.ppke.itk.cnctoolpathsharing.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ppke.itk.cnctoolpathsharing.domain.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolPathDto {

    private Integer id;
    private String name;
    private Integer createdByUserId;
    private LocalDateTime dateOfCreation;

    public static ToolPathDto fromToolPath(ToolPath toolPath) {
        return new ToolPathDto(
                toolPath.getId(),
                toolPath.getName(),
                toolPath.getCreatedByUser().getId(),
                toolPath.getDateOfCreation()
        );
    }
}
