package com.example.taskapp.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskapp.backend.entity.TaskAssignee;
import com.example.taskapp.backend.entity.TaskAssigneeId;

import java.util.List;

public interface TaskAssigneeRepository
        extends JpaRepository<TaskAssignee, TaskAssigneeId> {

	List<TaskAssignee> findByTask_Id(Long taskId);
	void deleteByTask_Id(Long taskId);

}

