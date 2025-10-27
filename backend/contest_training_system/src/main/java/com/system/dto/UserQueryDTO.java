package com.system.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NonNull;

/**
 * DTO for querying contests with pagination and filters.
 * 用于封装查询赛事列表时的分页和筛选条件
 */

@Data
public class UserQueryDTO {

    @NonNull
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;

    @NonNull
    @Min(value = 1, message = "每页数量不能小于1")
    private int pageSize = 10;


    private String keyword;    // Search by title
    private String role;       // "STUDENT", "ADMIN","TEACHER"
    private Integer isActive;     // 0 or 1
    private String sortBy;  // e.g., "START_TIME_DESC", "CREATED_AT_ASC"


}

