package com.mrlonis.todo.todo_service.repositories;

import com.mrlonis.todo.todo_service.entities.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "data", path = "todo-items")
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {}
