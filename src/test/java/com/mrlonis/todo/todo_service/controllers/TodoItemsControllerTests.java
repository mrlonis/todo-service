package com.mrlonis.todo.todo_service.controllers;

import com.mrlonis.todo.todo_service.TestUtils;
import com.mrlonis.todo.todo_service.entities.TodoItem;
import com.mrlonis.todo.todo_service.repositories.PrUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TestingUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TodoItemRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TodoItemsControllerTests {
    private static final String FAKE = "fake";

    @LocalServerPort
    private int port;

    private static WebClient webClient;

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
        todoItemRepository.flush();
        prUrlRepository.deleteAll();
        prUrlRepository.flush();
        testingUrlRepository.deleteAll();
        testingUrlRepository.flush();
    }

    @Test
    void testGetAllTodoItems_whenThereAreNoTodoItems() {
        TestUtils.callApiAndAssert(webClient, null, null, null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsAnEmptyTodoItem() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, null, false, null);
        TestUtils.callApiAndAssert(webClient, todoItem, null, null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsJiraUrl() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, FAKE, null, null, false, null);
        TestUtils.callApiAndAssert(webClient, todoItem, null, null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsOnePrUrl() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, null, false, null);
        var prUrl = TestUtils.createSaveAndAssertPrUrl(prUrlRepository, todoItem);
        TestUtils.callApiAndAssert(webClient, todoItem, List.of(prUrl), null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsMoreThanOnePrUrl() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, null, false, null);
        var prUrl1 = TestUtils.createSaveAndAssertPrUrl(prUrlRepository, todoItem);
        var prUrl2 = TestUtils.createSaveAndAssertPrUrl(prUrlRepository, todoItem);
        var prUrl3 = TestUtils.createSaveAndAssertPrUrl(prUrlRepository, todoItem);
        TestUtils.callApiAndAssert(webClient, todoItem, List.of(prUrl1, prUrl2, prUrl3), null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsCloudForgeConsoleUrl() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, FAKE, null, false, null);
        TestUtils.callApiAndAssert(webClient, todoItem, null, null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsReleaseRequestUrl() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, FAKE, false, null);
        TestUtils.callApiAndAssert(webClient, todoItem, null, null);
    }

    @Test
    void testGetAllTodoItems_whenThereIsOneTestingUrl() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, null, false, null);
        var testingUrl = TestUtils.createSaveAndAssertTestingUrl(testingUrlRepository, todoItem);
        TestUtils.callApiAndAssert(webClient, todoItem, null, List.of(testingUrl));
    }

    @Test
    void testGetAllTodoItems_whenThereIsMoreThanOneTestingUrl() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, null, false, null);
        var testingUrl1 = TestUtils.createSaveAndAssertTestingUrl(testingUrlRepository, todoItem);
        var testingUrl2 = TestUtils.createSaveAndAssertTestingUrl(testingUrlRepository, todoItem);
        var testingUrl3 = TestUtils.createSaveAndAssertTestingUrl(testingUrlRepository, todoItem);
        TestUtils.callApiAndAssert(webClient, todoItem, null, List.of(testingUrl1, testingUrl2, testingUrl3));
    }

    @Test
    void testGetAllTodoItems_whenTodoItemIsCompleted() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, null, true, null);
        TestUtils.callApiAndAssert(webClient, todoItem, null, null);
    }

    @Test
    void testGetAllTodoItems_whenTodoItemHasOneNoteUrl() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, null, false, FAKE);
        TestUtils.callApiAndAssert(webClient, todoItem, null, null);
    }
}
