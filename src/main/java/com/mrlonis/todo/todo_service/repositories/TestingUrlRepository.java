package com.mrlonis.todo.todo_service.repositories;

import com.mrlonis.todo.todo_service.entities.TestingUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "data", path = "testing-urls")
public interface TestingUrlRepository extends JpaRepository<TestingUrl, Integer> {}