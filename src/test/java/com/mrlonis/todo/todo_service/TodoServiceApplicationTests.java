package com.mrlonis.todo.todo_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.mrlonis.todo.todo_service.entities.PrUrl;
import com.mrlonis.todo.todo_service.entities.TodoItem;
import com.mrlonis.todo.todo_service.repositories.PrUrlRepository;
import com.mrlonis.todo.todo_service.repositories.TodoItemRepository;
import java.util.List;
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
class TodoServiceApplicationTests {
    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private PrUrlRepository prUrlRepository;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @AfterEach
    void tearDown() {
        todoItemRepository.deleteAll();
        prUrlRepository.deleteAll();
    }

    @Test
    void contextLoads() {}

    @Test
    void testGetAllTodoItems_whenThereAreNoTodoItems() {
        ParameterizedTypeReference<List<TodoItem>> type = new ParameterizedTypeReference<>() {};
        List<TodoItem> todoItems = webClient
                .get()
                .uri("/api/todo/items")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(type)
                .block();
        assertNotNull(todoItems);
    }

    @Test
    void testGetAllTodoItems_whenThereAreTodoItems_withOnePrUrl() {
        TodoItem todoItem = TodoItem.builder().build();
        todoItem = todoItemRepository.saveAndFlush(todoItem);
        assertNotNull(todoItem);
        assertNotNull(todoItem.getId());

        PrUrl prUrl = PrUrl.builder().todoItemId(todoItem.getId()).url("fake").build();
        prUrl = prUrlRepository.saveAndFlush(prUrl);

        ParameterizedTypeReference<List<TodoItem>> type = new ParameterizedTypeReference<>() {};
        List<TodoItem> todoItems = webClient
                .get()
                .uri("/api/todo/items")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(type)
                .block();
        assertNotNull(todoItems);
        assertEquals(1, todoItems.size());
        assertEquals(todoItem.getId(), todoItems.getFirst().getId());
        assertEquals(1, todoItems.getFirst().getPrUrls().size());
        assertEquals(prUrl.getUrl(), todoItems.getFirst().getPrUrls().getFirst());
    }

    @Test
    void testGetAllTodoItems_whenThereAreTodoItems_withMoreThanOnePrUrl() {
        TodoItem todoItem = TodoItem.builder().build();
        todoItem = todoItemRepository.saveAndFlush(todoItem);
        assertNotNull(todoItem);
        assertNotNull(todoItem.getId());

        PrUrl prUrl1 = PrUrl.builder().todoItemId(todoItem.getId()).url("fake1").build();
        prUrl1 = prUrlRepository.saveAndFlush(prUrl1);

        PrUrl prUrl2 = PrUrl.builder().todoItemId(todoItem.getId()).url("fake2").build();
        prUrl2 = prUrlRepository.saveAndFlush(prUrl2);

        PrUrl prUrl3 = PrUrl.builder().todoItemId(todoItem.getId()).url("fake3").build();
        prUrl3 = prUrlRepository.saveAndFlush(prUrl3);

        ParameterizedTypeReference<List<TodoItem>> type = new ParameterizedTypeReference<>() {};
        List<TodoItem> todoItems = webClient
                .get()
                .uri("/api/todo/items")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(type)
                .block();
        assertNotNull(todoItems);
        assertEquals(1, todoItems.size());
        assertEquals(todoItem.getId(), todoItems.getFirst().getId());
        assertEquals(3, todoItems.getFirst().getPrUrls().size());
        assertEquals(prUrl1.getUrl(), todoItems.getFirst().getPrUrls().getFirst());
        assertEquals(prUrl2.getUrl(), todoItems.getFirst().getPrUrls().get(1));
        assertEquals(prUrl3.getUrl(), todoItems.getFirst().getPrUrls().get(2));
    }
}
