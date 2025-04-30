package com.example.todo_java.dto;

import com.example.todo_java.model.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "O título é obrigatório.")
    private String title;

    private String description;

    private Boolean done = false;

    @NotNull(message = "A prioridade é obrigatória.")
    private Priority priority;
}
