package com.eduardocarlos.productivityApp.repositories;

import com.eduardocarlos.productivityApp.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUser_Id(Long id);

}
