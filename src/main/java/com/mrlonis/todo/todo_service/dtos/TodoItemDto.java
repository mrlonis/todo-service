package com.mrlonis.todo.todo_service.dtos;

import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.checkerframework.checker.nullness.qual.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
@Builder
public class TodoItemDto {
    private Long id;
    private String title;

    @Nullable private String jiraUrl;

    @Builder.Default
    private List<String> prUrls = new LinkedList<>();

    @Nullable private String cloudForgeConsoleUrl;

    @Nullable private String releaseRequestUrl;

    @Builder.Default
    private List<String> urlsUsedForTesting = new LinkedList<>();

    private boolean completed;

    @Nullable private String oneNoteUrl;
}
