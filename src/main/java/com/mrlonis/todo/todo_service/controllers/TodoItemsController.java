package com.mrlonis.todo.todo_service.controllers;

import com.mrlonis.todo.todo_service.entities.TodoItem;
import com.mrlonis.todo.todo_service.repositories.TodoItemRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todo")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TodoItemsController {
    private TodoItemRepository todoItemRepository;

    @GetMapping("/items")
    public List<TodoItem> getItems() {
        return todoItemRepository.findAll();
    }
}
