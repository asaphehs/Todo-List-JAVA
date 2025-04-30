package com.example.todo_java.service;

import com.example.todo_java.dto.TaskRequest;
import com.example.todo_java.dto.TaskResponse;
import com.example.todo_java.model.Task;
import com.example.todo_java.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Listar todas as tarefas
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Buscar por ID
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa nao encontrada com ID: " + id));
        return toResponse(task);
    }

    // Criar nova tarefa
    public TaskResponse createTask(TaskRequest request) {
        Task task = toEntity(request);
        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    // Atualizar tarefa existente
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDone(request.getDone());

        Task updated = taskRepository.save(task);
        return toResponse(updated);
    }

    // Deletar tarefa
    public void deletTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarefa nao encontrada com ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    // Métodos para auxiliar na conversão entre DTO e Entidade

    private Task toEntity(TaskRequest request) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .done(request.getDone())
                .priority(request.getPriority())
                .build();
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .done(task.getDone())
                .priority(task.getPriority())
                .build();
    }
}
