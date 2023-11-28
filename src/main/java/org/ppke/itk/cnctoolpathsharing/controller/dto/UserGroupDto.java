package org.ppke.itk.cnctoolpathsharing.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ppke.itk.cnctoolpathsharing.domain.ToolPath;
import org.ppke.itk.cnctoolpathsharing.domain.User;
import org.ppke.itk.cnctoolpathsharing.domain.UserGroup;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupDto {

    private Integer id;
    private String name;
    private Integer managedByUserId;
    private List<Integer> groupMemberIds;
    private List<Integer> toolPathsById;
    private List<Integer> membershipByGroupId;

    public static UserGroupDto fromUserGroup(UserGroup userGroup) {
        return new UserGroupDto(
                userGroup.getId(),
                userGroup.getName(),
                userGroup.getManagedByUser().getId(),
                userGroup.getGroupMembers().stream().map(User::getId).toList(),
                userGroup.getToolPaths().stream().map(ToolPath::getId).toList(),
                userGroup.getGroupMembers().stream().map(User::getId).toList()
        );
    }
}
