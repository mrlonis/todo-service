package com.mrlonis.todo.todo_service.services;

import static java.util.stream.Collectors.groupingBy;

import com.mrlonis.todo.todo_service.dtos.TodoItemDto;
import com.mrlonis.todo.todo_service.entities.PrUrl;
import com.mrlonis.todo.todo_service.entities.TestingUrl;
import com.mrlonis.todo.todo_service.entities.TodoItem;
import com.mrlonis.todo.todo_service.mappers.TodoItemMapper;
import com.mrlonis.todo.todo_service.repositories.PrUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TestingUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TodoItemRepository;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TodoItemService {
    private TodoItemRepository todoItemRepository;
    private PrUrlRepository prUrlRepository;
    private TestingUrlRepository testingUrlRepository;

    public List<TodoItemDto> getTodoItems() {
        return todoItemRepository.findAll().stream()
                .map(TodoItemMapper::mapTodoItemToTodoItemDto)
                .toList();
    }

    public TodoItem createOrUpdateTodoItem(TodoItemDto todoItemDto) {
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

    public Map<String, List<TodoItemDto>> getTodoItemsByPi() {
        return todoItemRepository.findAll().stream()
                .map(TodoItemMapper::mapTodoItemToTodoItemDto)
                .collect(groupingBy(TodoItemDto::getPi));
    }
}
