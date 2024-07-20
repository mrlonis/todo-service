package com.mrlonis.todo.todo_service.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mrlonis.todo.todo_service.entities.PrUrl;
import com.mrlonis.todo.todo_service.entities.TestingUrl;
import com.mrlonis.todo.todo_service.entities.TodoItem;
import com.mrlonis.todo.todo_service.repositories.PrUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TestingUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TodoItemRepository;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TodoItemsControllerTests {
    private static final String FAKE = "fake";

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private PrUrlRepository prUrlRepository;

    @Autowired
    private TestingUrlRepository testingUrlRepository;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @AfterEach
    void tearDown() {
        todoItemRepository.deleteAll();
        prUrlRepository.deleteAll();
        testingUrlRepository.deleteAll();
    }

    @Test
    void testGetAllTodoItems_whenThereAreNoTodoItems() {
        callApiAndAssert(null, null, null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsAnEmptyTodoItem() {
        TodoItem todoItem = createSaveAndAssertTodoItem(FAKE, null, null, null, false, null);
        callApiAndAssert(todoItem, null, null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsOnePrUrl() {
        TodoItem todoItem = createSaveAndAssertTodoItem(FAKE, null, null, null, false, null);
        var prUrl = createSaveAndAssertPrUrl(todoItem);
        callApiAndAssert(todoItem, List.of(prUrl), null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsMoreThanOnePrUrl() {
        TodoItem todoItem = createSaveAndAssertTodoItem(FAKE, null, null, null, false, null);
        var prUrl1 = createSaveAndAssertPrUrl(todoItem);
        var prUrl2 = createSaveAndAssertPrUrl(todoItem);
        var prUrl3 = createSaveAndAssertPrUrl(todoItem);
        callApiAndAssert(todoItem, List.of(prUrl1, prUrl2, prUrl3), null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsCloudForgeConsoleUrl() {
        TodoItem todoItem = createSaveAndAssertTodoItem(FAKE, null, FAKE, null, false, null);
        callApiAndAssert(todoItem, null, null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsReleaseRequestUrl() {
        TodoItem todoItem = createSaveAndAssertTodoItem(FAKE, null, null, FAKE, false, null);
        callApiAndAssert(todoItem, null, null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsOneTestingUrl() {
        TodoItem todoItem = createSaveAndAssertTodoItem(FAKE, null, null, null, false, null);
        var testingUrl = createSaveAndAssertTestingUrl(todoItem);
        callApiAndAssert(todoItem, null, List.of(testingUrl));
    }

    @Test
    void testGetAllTodoItems_whenThereIsMoreThanOneTestingUrl() {
        TodoItem todoItem = createSaveAndAssertTodoItem(FAKE, null, null, null, false, null);
        var testingUrl1 = createSaveAndAssertTestingUrl(todoItem);
        var testingUrl2 = createSaveAndAssertTestingUrl(todoItem);
        var testingUrl3 = createSaveAndAssertTestingUrl(todoItem);
        callApiAndAssert(todoItem, null, List.of(testingUrl1, testingUrl2, testingUrl3));
    }

    @Test
    void testGetAllTodoItems_whenTodoItemIsCompleted() {
        TodoItem todoItem = createSaveAndAssertTodoItem(FAKE, null, null, null, true, null);
        callApiAndAssert(todoItem, null, null);
    }

    @Test
    void testGetAllTodoItems_whenTodoItemHasOneNoteUrl() {
        TodoItem todoItem = createSaveAndAssertTodoItem(FAKE, null, null, null, false, FAKE);
        callApiAndAssert(todoItem, null, null);
    }

    private TodoItem createSaveAndAssertTodoItem(
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
        return todoItem;
    }

    private PrUrl createSaveAndAssertPrUrl(TodoItem todoItem) {
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

    private TestingUrl createSaveAndAssertTestingUrl(TodoItem todoItem) {
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

    private void callApiAndAssert(
            @Nullable TodoItem todoItem, @Nullable List<PrUrl> prUrls, @Nullable List<TestingUrl> testingUrls) {
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
            return;
        } else {
            assertEquals(1, todoItems.size());
        }

        TodoItem actualTodoItem = todoItems.getFirst();
        assertEquals(todoItem.getId(), actualTodoItem.getId());
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
    }
}
