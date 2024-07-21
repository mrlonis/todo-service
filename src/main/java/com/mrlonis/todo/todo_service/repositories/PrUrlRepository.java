package com.mrlonis.todo.todo_service.repositories;

import com.mrlonis.todo.todo_service.entities.PrUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "data", path = "pr-urls")
public interface PrUrlRepository extends JpaRepository<PrUrl, Long> {}
