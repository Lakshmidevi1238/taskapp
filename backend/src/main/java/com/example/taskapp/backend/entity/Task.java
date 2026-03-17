package com.example.taskapp.backend.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Table(name = "tasks",
       indexes = {
           @Index(name = "idx_tasks_list", columnList = "list_id"),
           @Index(name = "idx_tasks_board", columnList = "board_id"),
           @Index(name = "idx_tasks_list_position", columnList = "list_id, position"),
           @Index(name = "idx_tasks_title", columnList = "title")
       })
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ListEntity list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private java.util.List<TaskAssignee> assignees;

    


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Task() {}

    // ===== getters setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ListEntity getList() { return list; }
    public void setList(ListEntity list) { this.list = list; }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
   
    public java.util.List<TaskAssignee> getAssignees() {
        return assignees;
    }
}
