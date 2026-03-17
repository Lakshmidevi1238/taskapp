package com.example.taskapp.backend.service;

import com.example.taskapp.backend.entity.ListEntity;

import java.util.List;

public interface ListService {

    ListEntity createList(Long boardId, String title);

    void deleteList(Long listId);

    List<ListEntity> getBoardLists(Long boardId);

    void reorderLists(Long boardId, List<Long> orderedIds);
    ListEntity renameList(Long listId, String title, Long userId);


}
