package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.entity.*;
import org.springframework.data.domain.PageImpl;
import com.example.taskapp.backend.exception.BadRequestException;
import com.example.taskapp.backend.exception.NotFoundException;
import com.example.taskapp.backend.repository.*;
import com.example.taskapp.backend.service.ActivityService;
import com.example.taskapp.backend.service.RealtimeEventService;
import com.example.taskapp.backend.service.TaskService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepo;
    private final ListEntityRepository listRepo;
    private final UserRepository userRepo;
    private final TaskAssigneeRepository assigneeRepo;
    private final ActivityService activity;
    private final RealtimeEventService realtime;
    private final RedisTemplate<String, Object> redisTemplate;
    public TaskServiceImpl(
            TaskRepository taskRepo,
            ListEntityRepository listRepo,
            UserRepository userRepo,
            TaskAssigneeRepository assigneeRepo,
            ActivityService activity,
            RealtimeEventService realtime,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.taskRepo = taskRepo;
        this.listRepo = listRepo;
        this.userRepo = userRepo;
        this.assigneeRepo = assigneeRepo;
        this.activity = activity;
        this.realtime = realtime;
        this.redisTemplate = redisTemplate;
    }
    private void clearTaskCache() {
        Set<String> keys = redisTemplate.keys("tasks:search:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ================= CREATE =================

    @Override
    @Transactional
    public Task createTask(Long listId, String title, String desc, Long userId) {

        if (listId == null || userId == null) {
            throw new BadRequestException("List id and user id are required");
        }

        if (title == null || title.isBlank()) {
            throw new BadRequestException("Task title cannot be empty");
        }

        ListEntity list = listRepo.findById(listId)
                .orElseThrow(() ->
                        new NotFoundException("List not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User not found"));

        List<Task> tasks =
                taskRepo.findByList_IdOrderByPositionAsc(listId);

        Task t = new Task();
        t.setList(list);
        t.setBoard(list.getBoard());
        t.setTitle(title);
        t.setDescription(desc);
        t.setPosition(tasks.size());
        t.setCreatedBy(user);

        t = taskRepo.save(t);
        clearTaskCache();

        Long boardId = list.getBoard().getId();

        activity.log(boardId, userId,
                "TASK_CREATED", "TASK", t.getId(), null);

        realtime.publish(boardId,
                "TASK_CREATED",
                Map.of(
                        "taskId", t.getId(),
                        "listId", listId
                ));

        return t;
    }

    // ================= UPDATE (RENAME) =================

    @Override
    @Transactional
    public Task updateTask(Long taskId, String title, String desc) {

        if (taskId == null) {
            throw new BadRequestException("Task id cannot be null");
        }

        Task t = taskRepo.findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException("Task not found"));

        if (title != null && !title.isBlank()) {
            t.setTitle(title);
        }

        t.setDescription(desc);

        t = taskRepo.save(t);
        clearTaskCache();

        Long boardId = t.getBoard().getId();

        // 🔥 Activity log added
        activity.log(
        	    t.getBoard().getId(),
        	    null,
        	    "TASK_UPDATED",
        	    "TASK",
        	    taskId,
        	    null
        	);


        // 🔥 Publish correct event
        realtime.publish(
                boardId,
                "TASK_UPDATED",
                Map.of(
                        "taskId", taskId,
                        "title", t.getTitle()
                )
        );

        return t;
    }

    // ================= DELETE =================

    @Override
    @Transactional
    public void deleteTask(Long taskId) {

        if (taskId == null) {
            throw new BadRequestException("Task id cannot be null");
        }

        Task t = taskRepo.findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException("Task not found"));

        Long boardId = t.getBoard().getId();

        assigneeRepo.deleteByTask_Id(taskId);
        taskRepo.delete(t);
        clearTaskCache();

        activity.log(boardId, null,
                "TASK_DELETED", "TASK", taskId, null);

        realtime.publish(boardId,
                "TASK_DELETED",
                Map.of("taskId", taskId));
    }

    // ================= MOVE =================

    @Override
    @Transactional
    public Task moveTask(Long taskId, Long toListId, int newPos) {

        if (taskId == null || toListId == null) {
            throw new BadRequestException("Task id and target list id are required");
        }

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException("Task not found"));

        ListEntity targetList = listRepo.findById(toListId)
                .orElseThrow(() ->
                        new NotFoundException("Target list not found"));

        List<Task> targetTasks =
                taskRepo.findByList_IdOrderByPositionAsc(toListId);

        targetTasks.removeIf(t -> t.getId().equals(taskId));

        if (newPos < 0) newPos = 0;
        if (newPos > targetTasks.size())
            newPos = targetTasks.size();

        targetTasks.add(newPos, task);

        int pos = 0;
        for (Task t : targetTasks) {
            t.setPosition(pos++);
            t.setList(targetList);
            t.setBoard(targetList.getBoard());
        }

        taskRepo.saveAll(targetTasks);
        clearTaskCache();
        activity.log(
        	    targetList.getBoard().getId(),
        	    null,
        	    "TASK_MOVED",
        	    "TASK",
        	    taskId,
        	    null
        	);


        realtime.publish(
                targetList.getBoard().getId(),
                "TASK_MOVED",
                Map.of(
                        "taskId", taskId,
                        "toList", toListId,
                        "position", newPos
                )
        );

        return task;
    }

    // ================= SEARCH =================

    @Override
    public Page<Task> search(Long boardId,
                             String query,
                             int page,
                             int size) {

        if (boardId == null) {
            throw new BadRequestException("Board id cannot be null");
        }

        String key = "tasks:search:" + boardId + ":" + query + ":" + page + ":" + size;

        // 1. Check Redis
        List<Task> cached = (List<Task>) redisTemplate.opsForValue().get(key);

        if (cached != null) {
            System.out.println("FROM REDIS ✅");
            return new PageImpl<>(cached, PageRequest.of(page, size), cached.size());
        }

        Pageable p = PageRequest.of(
                page, size, Sort.by("createdAt").descending());

        Page<Task> result;

        if (query == null || query.isBlank()) {
            result = taskRepo.findByBoard_Id(boardId, p);
        } else {
            result = taskRepo
                    .findByBoard_IdAndTitleContainingIgnoreCase(
                            boardId, query, p);
        }

        // 2. Store in Redis
        List<Task> content = result.getContent();
        redisTemplate.opsForValue().set(key, content, 5, TimeUnit.MINUTES);

        System.out.println("FROM DB ❌");

        return result;
    }
}
