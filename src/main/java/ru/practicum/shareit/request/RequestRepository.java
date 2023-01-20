package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    Page<Request> findByRequesterId(int userId, Pageable page);

    Page<Request> findByRequesterIdNot(int userId, Pageable page);
}