package com.mrlonis.todo.todo_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
class TodoServiceApplicationTests {
    @Test
    void contextLoads() {}
}

@SpringBootTest
@ActiveProfiles("test")
class TodoServiceApplicationInMemoryDbTests {
    @Test
    void contextLoads() {}
}
