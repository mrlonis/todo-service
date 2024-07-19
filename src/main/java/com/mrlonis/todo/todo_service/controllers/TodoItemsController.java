package com.mrlonis.todo.todo_service.controllers;

import com.mrlonis.todo.todo_service.entities.TodoItem;
import com.mrlonis.todo.todo_service.repositories.TodoItemRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todo")
@AllArgsConstructor
public class TodoItemsController {
    private TodoItemRepository todoItemRepository;

    @GetMapping("/items")
    public List<TodoItem> getItems() {
        return todoItemRepository.findAll();
    }
}
