package org.ppke.itk.cnctoolpathsharing.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ppke.itk.cnctoolpathsharing.domain.MachineTool;
import org.ppke.itk.cnctoolpathsharing.domain.ToolPath;
import org.ppke.itk.cnctoolpathsharing.domain.User;
import org.ppke.itk.cnctoolpathsharing.domain.UserGroup;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Integer id;
    private String name;
    private String emailAddress;
    private List<Integer> machineToolsById;
    private List<Integer> toolPathsById;
    private List<Integer> membershipByGroupId;

    public static UserDto fromUser(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmailAddress(),
                user.getMachineTools().stream().map(MachineTool::getId).toList(),
                user.getToolPaths().stream().map(ToolPath::getId).toList(),
                user.getMembership().stream().map(UserGroup::getId).toList()
        );
    }
}
