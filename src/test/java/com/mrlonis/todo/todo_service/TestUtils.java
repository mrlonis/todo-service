package com.mrlonis.todo.todo_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mrlonis.todo.todo_service.entities.PrUrl;
import com.mrlonis.todo.todo_service.entities.TestingUrl;
import com.mrlonis.todo.todo_service.entities.TodoItem;
import com.mrlonis.todo.todo_service.enums.TodoItemType;
import com.mrlonis.todo.todo_service.repositories.PrUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TestingUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TodoItemRepository;
import com.mrlonis.todo.todo_service.utils.Constants;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@UtilityClass
public class TestUtils {
    public static long TODO_ITEM_COUNT = 0;
    public static final String FAKE = "fake";
    public static final ZonedDateTime CREATED_ON = ZonedDateTime.now();
    public static final int SPRINT = 1;

    public static void incrementTodoItemCount() {
        TODO_ITEM_COUNT++;
    }

    public static TodoItem createSaveAndAssertTodoItem(
            TodoItemRepository todoItemRepository,
            String title,
            @Nullable String jiraUrl,
            @Nullable String cloudForgeConsoleUrl,
            @Nullable String releaseRequestUrl,
            boolean completed,
            @Nullable String oneNoteUrl) {
        TodoItem todoItem = TodoItem.builder()
                .title(title)
                .jiraUrl(jiraUrl)
                .cloudForgeConsoleUrl(cloudForgeConsoleUrl)
                .releaseRequestUrl(releaseRequestUrl)
                .completed(completed)
                .oneNoteUrl(oneNoteUrl)
                .createdOn(CREATED_ON)
                .pi(FAKE)
                .sprint(SPRINT)
                .type(TodoItemType.ASSIGNED)
                .build();
        todoItem = todoItemRepository.saveAndFlush(todoItem);
        assertNotNull(todoItem);
        assertNotNull(todoItem.getId());
        if (cloudForgeConsoleUrl != null) {
            assertNotNull(todoItem.getCloudForgeConsoleUrl());
            assertEquals(cloudForgeConsoleUrl, todoItem.getCloudForgeConsoleUrl());
        } else {
            assertNull(todoItem.getCloudForgeConsoleUrl());
        }
        if (releaseRequestUrl != null) {
            assertNotNull(todoItem.getReleaseRequestUrl());
            assertEquals(releaseRequestUrl, todoItem.getReleaseRequestUrl());
        } else {
            assertNull(todoItem.getReleaseRequestUrl());
        }
        assertEquals(completed, todoItem.isCompleted());
        if (oneNoteUrl != null) {
            assertNotNull(todoItem.getOneNoteUrl());
            assertEquals(oneNoteUrl, todoItem.getOneNoteUrl());
        } else {
            assertNull(todoItem.getOneNoteUrl());
        }
        assertEquals(CREATED_ON, todoItem.getCreatedOn());
        assertEquals(FAKE, todoItem.getPi());
        assertEquals(SPRINT, todoItem.getSprint());
        assertEquals(TodoItemType.ASSIGNED, todoItem.getType());
        return todoItem;
    }

    public static PrUrl createSaveAndAssertPrUrl(PrUrlRepository prUrlRepository, TodoItem todoItem) {
        PrUrl prUrl = PrUrl.builder().todoItemId(todoItem.getId()).url(FAKE).build();
        prUrl = prUrlRepository.saveAndFlush(prUrl);
        assertNotNull(prUrl);
        assertNotNull(prUrl.getId());
        assertNotNull(prUrl.getTodoItemId());
        assertEquals(todoItem.getId(), prUrl.getTodoItemId());
        assertNotNull(prUrl.getUrl());
        assertEquals(FAKE, prUrl.getUrl());
        return prUrl;
    }

    public static TestingUrl createSaveAndAssertTestingUrl(
            TestingUrlRepository testingUrlRepository, TodoItem todoItem) {
        TestingUrl testingUrl =
                TestingUrl.builder().todoItemId(todoItem.getId()).url(FAKE).build();
        testingUrl = testingUrlRepository.saveAndFlush(testingUrl);
        assertNotNull(testingUrl);
        assertNotNull(testingUrl.getId());
        assertNotNull(testingUrl.getTodoItemId());
        assertEquals(todoItem.getId(), testingUrl.getTodoItemId());
        assertNotNull(testingUrl.getUrl());
        assertEquals(FAKE, testingUrl.getUrl());
        return testingUrl;
    }

    public static boolean callApiAndAssert(
            WebClient webClient,
            @Nullable TodoItem todoItem,
            @Nullable List<PrUrl> prUrls,
            @Nullable List<TestingUrl> testingUrls) {
        ParameterizedTypeReference<List<TodoItem>> type = new ParameterizedTypeReference<>() {};
        List<TodoItem> todoItems = webClient
                .get()
                .uri("/api/todo/items")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(type)
                .block();
        assertNotNull(todoItems);
        if (todoItem == null) {
            assertTrue(todoItems.isEmpty());
            return true;
        } else {
            assertEquals(1, todoItems.size());
        }

        TodoItem actualTodoItem = todoItems.getFirst();
        assertEquals(todoItem.getId(), actualTodoItem.getId());
        assertEquals(todoItem.getTitle(), actualTodoItem.getTitle());
        assertEquals(todoItem.getJiraUrl(), actualTodoItem.getJiraUrl());
        if (prUrls == null) {
            assertNotNull(actualTodoItem.getPrUrls());
            assertTrue(actualTodoItem.getPrUrls().isEmpty());
        } else {
            assertEquals(prUrls.size(), actualTodoItem.getPrUrls().size());
            for (int i = 0; i < todoItem.getPrUrls().size(); i++) {
                assertEquals(prUrls.get(i).getUrl(), actualTodoItem.getPrUrls().get(i));
            }
        }
        assertEquals(todoItem.getCloudForgeConsoleUrl(), actualTodoItem.getCloudForgeConsoleUrl());
        assertEquals(todoItem.getReleaseRequestUrl(), actualTodoItem.getReleaseRequestUrl());
        assertEquals(todoItem.isCompleted(), actualTodoItem.isCompleted());
        assertEquals(todoItem.getOneNoteUrl(), actualTodoItem.getOneNoteUrl());
        if (testingUrls == null) {
            assertNotNull(actualTodoItem.getUrlsUsedForTesting());
            assertTrue(actualTodoItem.getUrlsUsedForTesting().isEmpty());
        } else {
            assertEquals(
                    testingUrls.size(), actualTodoItem.getUrlsUsedForTesting().size());
            for (int i = 0; i < testingUrls.size(); i++) {
                assertEquals(
                        testingUrls.get(i).getUrl(),
                        actualTodoItem.getUrlsUsedForTesting().get(i));
            }
        }
        System.out.println(todoItem.getCreatedOn());
        System.out.println(actualTodoItem.getCreatedOn());
        assertEquals(
                todoItem.getCreatedOn().format(Constants.DATE_TIME_FORMATTER),
                actualTodoItem.getCreatedOn().format(Constants.DATE_TIME_FORMATTER));
        assertEquals(todoItem.getPi(), actualTodoItem.getPi());
        assertEquals(todoItem.getSprint(), actualTodoItem.getSprint());
        assertEquals(todoItem.getType(), actualTodoItem.getType());
        return true;
    }

    public static boolean callApiAndAssertJson(WebClient webClient, String expectedJson) {
        String todoItems = webClient
                .get()
                .uri("/api/todo/items")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        assertNotNull(todoItems);
        assertEquals(expectedJson, todoItems);
        return true;
    }
}
