package com.mrlonis.todo.todo_service.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mrlonis.todo.todo_service.TestUtils;
import com.mrlonis.todo.todo_service.entities.PrUrl;
import com.mrlonis.todo.todo_service.entities.TestingUrl;
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
    void testGetItems_whenThereAreNoTodoItems() {
        assertTrue(TestUtils.callApiAndAssert(webClient, null, null, null));
    }

    @Test
    void testGetItems_whenThereIsAnEmptyTodoItem() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, null, false, null);
        TestUtils.incrementTodoItemCount();
        assertTrue(TestUtils.callApiAndAssert(webClient, todoItem, null, null));
    }

    @Test
    void testGetItems_whenAllFieldsArePresent() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, FAKE, FAKE, FAKE, true, FAKE);
        TestUtils.incrementTodoItemCount();
        PrUrl prUrl = TestUtils.createSaveAndAssertPrUrl(prUrlRepository, todoItem);
        TestingUrl testingUrl = TestUtils.createSaveAndAssertTestingUrl(testingUrlRepository, todoItem);
        assertTrue(TestUtils.callApiAndAssert(webClient, todoItem, List.of(prUrl), List.of(testingUrl)));
    }

    @Test
    void testGetItems_whenAllFieldsArePresent_butUrlListsHaveMultiple() {
        TodoItem todoItem =
                TestUtils.createSaveAndAssertTodoItem(todoItemRepository, FAKE, null, null, null, false, null);
        TestUtils.incrementTodoItemCount();
        PrUrl prUrl1 = TestUtils.createSaveAndAssertPrUrl(prUrlRepository, todoItem);
        PrUrl prUrl2 = TestUtils.createSaveAndAssertPrUrl(prUrlRepository, todoItem);
        PrUrl prUrl3 = TestUtils.createSaveAndAssertPrUrl(prUrlRepository, todoItem);
        TestingUrl testingUrl1 = TestUtils.createSaveAndAssertTestingUrl(testingUrlRepository, todoItem);
        TestingUrl testingUrl2 = TestUtils.createSaveAndAssertTestingUrl(testingUrlRepository, todoItem);
        TestingUrl testingUrl3 = TestUtils.createSaveAndAssertTestingUrl(testingUrlRepository, todoItem);
        assertTrue(TestUtils.callApiAndAssert(
                webClient, todoItem, List.of(prUrl1, prUrl2, prUrl3), List.of(testingUrl1, testingUrl2, testingUrl3)));
    }
}
