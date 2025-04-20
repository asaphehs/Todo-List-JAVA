package com.example.todo_java.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse  {

    private Long id;
    private String title;
    private String description;
    private Boolean done;
}
