package com.mrlonis.todo.todo_service.controllers;

import com.mrlonis.todo.todo_service.dtos.TodoItemDto;
import com.mrlonis.todo.todo_service.entities.PrUrl;
import com.mrlonis.todo.todo_service.entities.TestingUrl;
import com.mrlonis.todo.todo_service.entities.TodoItem;
import com.mrlonis.todo.todo_service.repositories.PrUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TestingUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TodoItemRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todo")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TodoItemsController {
    private TodoItemRepository todoItemRepository;
    private PrUrlRepository prUrlRepository;
    private TestingUrlRepository testingUrlRepository;

    @GetMapping("/items")
    public List<TodoItemDto> getItems() {
        return todoItemRepository.findAll().stream()
                .map(todoItem -> TodoItemDto.builder()
                        .id(todoItem.getId())
                        .title(todoItem.getTitle())
                        .jiraUrl(todoItem.getJiraUrl())
                        .prUrls(todoItem.getPrUrls())
                        .cloudForgeConsoleUrl(todoItem.getCloudForgeConsoleUrl())
                        .releaseRequestUrl(todoItem.getReleaseRequestUrl())
                        .urlsUsedForTesting(todoItem.getUrlsUsedForTesting())
                        .completed(todoItem.isCompleted())
                        .oneNoteUrl(todoItem.getOneNoteUrl())
                        .createdOn(todoItem.getCreatedOn())
                        .pi(todoItem.getPi())
                        .sprint(todoItem.getSprint())
                        .type(todoItem.getType())
                        .build())
                .toList();
    }

    @PostMapping("/item")
    public TodoItem addItem(@RequestBody TodoItemDto todoItemDto) {
        log.info("Received the following TodoItemDto: {}", todoItemDto);
        TodoItem.TodoItemBuilder todoItemBuilder = TodoItem.builder()
                .title(todoItemDto.getTitle())
                .jiraUrl(todoItemDto.getJiraUrl())
                .cloudForgeConsoleUrl(todoItemDto.getCloudForgeConsoleUrl())
                .releaseRequestUrl(todoItemDto.getReleaseRequestUrl())
                .completed(todoItemDto.isCompleted())
                .oneNoteUrl(todoItemDto.getOneNoteUrl())
                .pi(todoItemDto.getPi())
                .sprint(todoItemDto.getSprint())
                .type(todoItemDto.getType());
        if (todoItemDto.getCreatedOn() != null) {
            todoItemBuilder.createdOn(todoItemDto.getCreatedOn());
        }
        TodoItem todoItem = todoItemBuilder.build();
        todoItem = todoItemRepository.saveAndFlush(todoItem);

        if (todoItemDto.getPrUrls() != null && !todoItemDto.getPrUrls().isEmpty()) {
            TodoItem finalTodoItem = todoItem;
            List<PrUrl> prUrls = todoItemDto.getPrUrls().stream()
                    .map(prUrl -> PrUrl.builder()
                            .url(prUrl)
                            .todoItemId(finalTodoItem.getId())
                            .build())
                    .toList();
            prUrlRepository.saveAllAndFlush(prUrls);
        }

        if (todoItemDto.getUrlsUsedForTesting() != null
                && !todoItemDto.getUrlsUsedForTesting().isEmpty()) {
            TodoItem finalTodoItem1 = todoItem;
            List<TestingUrl> testingUrls = todoItemDto.getUrlsUsedForTesting().stream()
                    .map(testingUrl -> TestingUrl.builder()
                            .url(testingUrl)
                            .todoItemId(finalTodoItem1.getId())
                            .build())
                    .toList();
            testingUrlRepository.saveAllAndFlush(testingUrls);
        }

        return todoItemRepository.getReferenceById(todoItem.getId());
    }
}
