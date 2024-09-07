package com.hwarrk.domain.project_join.dto.res;

import com.hwarrk.domain.project_join.entity.ProjectJoin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProjectJoinRes(
    @NotNull
    Long memberId,
    @NotNull
    Long projectId
) {
    public static ProjectJoinRes mapEntityToRes(ProjectJoin projectJoin) {
        return ProjectJoinRes.builder()
                .memberId(projectJoin.getMember().getId())
                .projectId(projectJoin.getProject().getId())
                .build();
    }
}