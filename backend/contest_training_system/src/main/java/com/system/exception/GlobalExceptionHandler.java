package com.system.exception;

import com.system.vo.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException ex) {
        // 打印异常信息到日志
        ex.printStackTrace();
        return Result.error(ex.getMessage());
    }

    // 可以添加处理其他类型异常的方法，例如参数校验异常等
    /**
     * 处理 JSON 请求体(@RequestBody) 的参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .collect(Collectors.toList());
        return Result.error(String.join("; ", messages));
    }

    /**
     * 处理表单提交或普通对象绑定的校验错误
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException ex) {
        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .collect(Collectors.toList());
        return Result.error(String.join("; ", messages));
    }

    /**
     * 处理单个参数(如 @RequestParam / @PathVariable) 的校验错误
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        return Result.error(msg);
    }

    /**
     * 处理数据库唯一约束冲突异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException ex) {
        // 打印异常堆栈到服务器日志，供开发人员排查
        ex.printStackTrace();

        // 默认的错误信息
        String friendlyMessage = "数据重复，请检查输入是否已存在。";

        try {
            // 获取底层 SQL 异常信息，通常是 java.sql.SQLIntegrityConstraintViolationException
            String rawMessage = ex.getCause().getMessage();

            // 检查是否包含 MySQL 的重复条目关键字
            if (rawMessage != null && rawMessage.contains("Duplicate entry") && rawMessage.contains("for key")) {

                // 1. 提取重复的值 (VALUE)
                // 找到第一个 ' 和 第二个 ' 之间的内容
                int firstQuote = rawMessage.indexOf("'");
                int secondQuote = rawMessage.indexOf("'", firstQuote + 1);
                String value = rawMessage.substring(firstQuote + 1, secondQuote);

                // 2. 提取重复的键/索引名 (KEY_NAME)
                // 找到 for key ' 和 最后一个 ' 之间的内容
                int keyStart = rawMessage.lastIndexOf("for key '") + 9;
                int keyEnd = rawMessage.lastIndexOf("'");
                String fullKeyName = rawMessage.substring(keyStart, keyEnd);

                // 尝试提取字段名 (通常在表名.字段名中，如 problem.title)
                String fieldName = fullKeyName;
                if (fullKeyName.contains(".")) {
                    // 如果是表名.字段名，只取字段名
                    fieldName = fullKeyName.substring(fullKeyName.indexOf(".") + 1);
                } else if (fullKeyName.contains("PRIMARY")) {
                    // 如果是主键
                    fieldName = "主键";
                }


                // 最终报告
                friendlyMessage = String.format("%s ：【%s】已存在，请勿重复添加。", fieldName, value);
            }

        } catch (Exception e) {
            // 如果解析过程中发生任何错误，仍然返回默认的友好信息
            System.err.println("解析 DuplicateKeyException 失败: " + e.getMessage());
        }

        return Result.error(friendlyMessage);
    }

    /**
     * 处理数据完整性约束异常（外键/非空/长度等）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ex.printStackTrace();
        String raw = getDeepestMessage(ex);
        String friendly = "数据约束冲突，操作失败。";

        if (raw != null) {
            // 外键删除/更新父记录失败
            if (raw.contains("Cannot delete or update a parent row") && raw.contains("foreign key constraint fails")) {
                // 提取子表、外键列、父表、父列
                // 示例：... fails (`db`.`training_plan_contest`, CONSTRAINT `training_plan_contest_ibfk_2` FOREIGN KEY (`contest_id`) REFERENCES `contest` (`contest_id`))
                Pattern p = Pattern.compile("fails \\(`[^`]+`\\.`([^`]+)`.*?FOREIGN KEY \\(`([^`]+)`\\) REFERENCES `([^`]+)` \\(`([^`]+)`\\)");
                Matcher m = p.matcher(raw);
                if (m.find()) {
                    String childTable = m.group(1);
                    String fkColumn = m.group(2);
                    String parentTable = m.group(3);
                    // String parentColumn = m.group(4);

                    // 已知业务语义的定制化提示
                    if ("contest".equalsIgnoreCase(parentTable) && "training_plan_contest".equalsIgnoreCase(childTable) && "contest_id".equalsIgnoreCase(fkColumn)) {
                        friendly = "该竞赛已被训练计划引用，无法删除，请先在训练计划中移除该竞赛。";
                    } else {
                        friendly = String.format("该数据被表 %s 的外键 %s 引用，无法删除，请先删除或解除关联。", childTable, fkColumn);
                    }
                } else {
                    friendly = "该数据被其他记录引用，无法删除，请先删除或解除关联。";
                }
            }
            // 插入/更新子记录失败（外键不存在）
            else if (raw.contains("Cannot add or update a child row") && raw.contains("foreign key constraint fails")) {
                friendly = "外键引用不存在，关联的父记录缺失，请检查并选择有效的关联数据。";
            }
            // 非空约束
            else if (raw.contains("cannot be null") && raw.contains("Column")) {
                // 示例：Column 'title' cannot be null
                Pattern p = Pattern.compile("Column '([^']+)' cannot be null");
                Matcher m = p.matcher(raw);
                if (m.find()) {
                    friendly = String.format("字段 %s 不能为空。", m.group(1));
                } else {
                    friendly = "存在必填字段为空，请完善后再试。";
                }
            }
            // 长度超限
            else if (raw.contains("Data too long for column")) {
                // 示例：Data too long for column 'title' at row 1
                Pattern p = Pattern.compile("Data too long for column '([^']+)'");
                Matcher m = p.matcher(raw);
                if (m.find()) {
                    friendly = String.format("字段 %s 超出长度限制，请精简内容。", m.group(1));
                } else {
                    friendly = "部分字段超出长度限制，请精简内容。";
                }
            }
            // 唯一约束（部分数据库驱动可能也走到这里）
            else if (raw.contains("Duplicate entry")) {
                friendly = "数据重复，请检查输入是否已存在。";
            }
        }

        return Result.error(friendly);
    }

    /**
     * 处理SQL异常
     */
    @ExceptionHandler(org.springframework.jdbc.BadSqlGrammarException.class)
    public Result handleBadSqlGrammarException(org.springframework.jdbc.BadSqlGrammarException ex) {
        // 打印异常堆栈到日志
        ex.printStackTrace();
        // 返回友好提示
        return Result.error("数据库操作异常");
    }

    // 获取最内层 cause 的 message
    private String getDeepestMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur.getMessage();
    }
}