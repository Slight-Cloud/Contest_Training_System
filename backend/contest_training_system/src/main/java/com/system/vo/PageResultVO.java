package com.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页查询结果的统一VO封装
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResultVO<T> {

    private Long total;       // 总记录数

    private Integer page;     // 当前页码

    private Integer pageSize; // 每页数量

    private List<T> list;     // 当前页数据列表
}