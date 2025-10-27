package com.system.entity;

/**
 * API排序顺序枚举
 * <p>
 * 该枚举类用于解析前端通过 sort_by 参数传入的排序请求，
 * 并将其转换为可供数据库查询使用的列名(column)和方向(direction)。
 * <p>
 * 它通过为不同业务实体（如用户、赛事、题目）提供专属的解析方法（如 forUser, forContest），
 * 解决了通用排序参数 'id_asc', 'id_desc' 在不同数据表中需要映射到不同主键字段（如 'user_id', 'contest_id'）的问题。
 */
public enum SortOrder {

    // region =========== 通用排序字段 ===========
    // 这些字段在多个API模块中都可能被使用

    /**
     * 按标题升序
     */
    TITLE_ASC("title", "ASC"),
    /**
     * 按标题降序
     */
    TITLE_DESC("title", "DESC"),

    /**
     * 按创建时间升序
     */
    CREATED_ASC("created_at", "ASC"),
    /**
     * 按创建时间降序
     */
    CREATED_DESC("created_at", "DESC"),

    /**
     * 按开始时间升序
     */
    START_TIME_ASC("start_time", "ASC"),
    /**
     * 按开始时间降序
     */
    START_TIME_DESC("start_time", "DESC"),

    // endregion

    // region =========== 用户 (/admin/users) ===========

    /**
     * 按用户ID升序 (对应API参数: id_asc)
     */
    USER_ID_ASC("user_id", "ASC"),
    /**
     * 按用户ID降序 (对应API参数: id_desc)
     */
    USER_ID_DESC("user_id", "DESC"),
    /**
     * 按昵称升序
     */
    NICKNAME_ASC("nickname", "ASC"),
    /**
     * 按昵称降序
     */
    NICKNAME_DESC("nickname", "DESC"),

    // endregion

    // region =========== 赛事 (/contest/list) ===========

    /**
     * 按赛事ID升序 (对应API参数: id_asc)
     */
    CONTEST_ID_ASC("contest_id", "ASC"),
    /**
     * 按赛事ID降序 (对应API参数: id_desc)
     */
    CONTEST_ID_DESC("contest_id", "DESC"),

    // endregion

    // region =========== 题目 (/problem/list) ===========

    /**
     * 按题目ID升序 (对应API参数: id_asc)
     */
    PROBLEM_ID_ASC("problem_id", "ASC"),
    /**
     * 按题目ID降序 (对应API参数: id_desc)
     */
    PROBLEM_ID_DESC("problem_id", "DESC"),

    // endregion

    // region =========== 提交 (/submission/list) ===========

    /**
     * 按提交ID升序 (对应API参数: id_asc)
     */
    SUBMISSION_ID_ASC("submission_id", "ASC"),
    /**
     * 按提交ID降序 (对应API参数: id_desc)
     */
    SUBMISSION_ID_DESC("submission_id", "DESC"),
    /**
     * 按运行时长升序
     */
    TIME_USED_ASC("time_used", "ASC"),
    /**
     * 按运行时长降序
     */
    TIME_USED_DESC("time_used", "DESC"),

    // endregion

    // region =========== 训练计划 (/training_plan/list) ===========
    // API文档未明确此接口的sort_by, 此处基于通用字段进行推断

    /**
     * 按计划ID升序 (对应API参数: id_asc)
     */
    PLAN_ID_ASC("plan_id", "ASC"),
    /**
     * 按计划ID降序 (对应API参数: id_desc)
     */
    PLAN_ID_DESC("plan_id", "DESC");

    // endregion


    private final String column;// 数据库列名
    private final String direction;// 排序方向 (ASC/DESC)

    SortOrder(String column, String direction) {
        this.column = column;
        this.direction = direction;
    }

    /**
     * 通用的实体排序解析器核心。
     * 优雅地处理了不同实体的排序逻辑共性。
     *
     * @param sortBy       前端传入的排序字符串 (例如 "id_asc", "title_desc")。
     * @param defaultOrder 如果 sortBy 为空或解析失败时，返回的默认排序规则。
     * @param idAsc        当 sortBy 为 "id_asc" 时，应映射到的具体枚举值 (如 USER_ID_ASC)。
     * @param idDesc       当 sortBy 为 "id_desc" 时，应映射到的具体枚举值 (如 USER_ID_DESC)。
     * @return 解析后的 SortOrder 枚举实例。
     */
    private static SortOrder forEntity(String sortBy, SortOrder defaultOrder, SortOrder idAsc, SortOrder idDesc) {

        // 处理空或无效的排序参数，返回默认排序
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return defaultOrder;
        }
        // 将前端参数统一转为大写，以匹配枚举名称
        String key = sortBy.toUpperCase();

        // 优先处理需要特殊映射的 'id' 别名
        if ("ID_ASC".equals(key)) {
            return idAsc;
        }
        else if ("ID_DESC".equals(key)) {
            return idDesc;
        }

        // 尝试按枚举名称直接匹配其余排序参数 (如 TITLE_ASC)
        try {
            return SortOrder.valueOf(key);
        } catch (IllegalArgumentException e) {
            // 如果无法匹配任何已定义的枚举，则返回该实体的默认排序
            return defaultOrder;
        }
    }

    /**
     * **用户列表 (/admin/users) 专用解析方法。**
     * <p>
     * <b>支持:</b> id_asc/desc, nickname_asc/desc, created_asc/desc
     * <p>
     * <b>默认:</b> USER_ID_ASC (按用户ID升序)
     */
    public static SortOrder forUser(String sortBy) {
        return forEntity(sortBy, USER_ID_ASC, USER_ID_ASC, USER_ID_DESC);
    }

    /**
     * **赛事列表 (/contest/list) 专用解析方法。**
     * <p>
     * <b>支持:</b> id_asc/desc, title_asc/desc, start_time_asc/desc
     * <p>
     * <b>默认:</b> CONTEST_ID_ASC (按赛事ID升序)
     */
    public static SortOrder forContest(String sortBy) {
        return forEntity(sortBy, CONTEST_ID_ASC, CONTEST_ID_ASC, CONTEST_ID_DESC);
    }

    /**
     * **题目列表 (/problem/list) 专用解析方法。**
     * <p>
     * <b>支持:</b> id_asc/desc, title_asc/desc, created_asc/desc
     * <p>
     * <b>默认:</b> PROBLEM_ID_ASC (按题目ID升序)
     */
    public static SortOrder forProblem(String sortBy) {
        return forEntity(sortBy, PROBLEM_ID_ASC, PROBLEM_ID_ASC, PROBLEM_ID_DESC);
    }

    /**
     * **提交记录列表 (/submission/list) 专用解析方法。**
     * <p>
     * <b>支持:</b> id_asc/desc, created_asc/desc, time_used_asc/desc
     * <p>
     * <b>默认:</b> SUBMISSION_ID_DESC (按提交ID降序，即最新提交在前)
     */
    public static SortOrder forSubmission(String sortBy) {
        return forEntity(sortBy, SUBMISSION_ID_DESC, SUBMISSION_ID_ASC, SUBMISSION_ID_DESC);
    }

    /**
     * **训练计划列表 (/training_plan/list) 专用解析方法。**
     * <p>
     * <b>注意:</b> API文档未定义此接口的排序参数。此处基于通用字段推断实现。
     * <p>
     * <b>支持 (推断):</b> id_asc/desc, title_asc/desc, start_time_asc/desc
     * <p>
     * <b>默认:</b> PLAN_ID_ASC (按计划ID升序)
     */
    public static SortOrder forTrainingPlan(String sortBy) {
        return forEntity(sortBy, PLAN_ID_ASC, PLAN_ID_ASC, PLAN_ID_DESC);
    }

    public String getColumn() {
        return column;
    }

    public String getDirection() {
        return direction;
    }
}