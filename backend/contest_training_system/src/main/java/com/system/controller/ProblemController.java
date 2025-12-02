package com.system.controller;

import com.system.dto.ProblemCreateDTO;
import com.system.dto.ProblemQueryDTO;
import com.system.dto.ProblemUpdateDTO;
import com.system.dto.SolutionCreateDTO;
import com.system.dto.SolutionUpdateDTO;
import com.system.entity.Problem;
import com.system.service.ProblemService;
import com.system.vo.PageResultVO;
import com.system.vo.ProblemListVO;
import com.system.vo.Result;
import com.system.vo.SolutionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    /**
     * 3.1 新建题目 - 符合文档
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Problem> createProblem(@RequestBody ProblemCreateDTO createDTO) {
        Problem newProblem = problemService.createProblem(createDTO);
        return Result.success(newProblem);
    }


    /**
     * 3.2 修改题目 - 需要调整
     * 文档要求：PUT /problem/update
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<String> updateProblem(@RequestBody ProblemUpdateDTO updateDTO) {
        problemService.updateProblem(updateDTO.getProblemId(), updateDTO);
        return Result.success("题目更新成功");
    }

    /**
     * 3.3 删除/隐藏题目 - 需要调整
     */
    @DeleteMapping("/{problemId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<String> deleteProblem(@PathVariable Long problemId) {
        String message = problemService.deleteOrHideProblem(problemId);
        return Result.success(message);
    }

    /**
     * 3.4 查询题目列表 - 符合文档
     * GET /problem/list?page=1&pageSize=10&keyword=xxx
     */
    @GetMapping("/list")
    public Result<PageResultVO<ProblemListVO>> getProblemList(ProblemQueryDTO queryDTO) {
        PageResultVO<ProblemListVO> pageResult = problemService.getProblemList(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 3.5 获取题目详情 - 需要调整
     */
    @GetMapping("{problemId}")
    public Result getProblemDetail(@PathVariable Long problemId) {
        return Result.success(problemService.getProblemDetail(problemId));
    }

    /**
     * 3.6 发布题解
     */
    @PostMapping("/solution/create")
    public Result<?> publishSolution(@RequestBody @Validated SolutionCreateDTO createDTO) {
        problemService.publishSolution(createDTO);
        return Result.success();
    }

    /**
     * 3.7 查询题解列表
     */
    @GetMapping("/{problemId}/solution/list")
    public Result<PageResultVO<SolutionVO>> getSolutionList(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(problemService.getSolutionList(problemId, page, pageSize));
    }

    /**
     * 3.8 获取题解详情
     */
    @GetMapping("/solution/{reportId}")
    public Result<SolutionVO> getSolutionDetail(@PathVariable Long reportId) {
        return Result.success(problemService.getSolutionDetail(reportId));
    }

    /**
     * 修改题解
     */
    @PutMapping("/solution/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public Result<?> updateSolution(@RequestBody @Validated SolutionUpdateDTO updateDTO) {
        problemService.updateSolution(updateDTO);
        return Result.success();
    }

    /**
     * 删除题解
     */
    @DeleteMapping("/solution/{reportId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public Result<?> deleteSolution(@PathVariable Long reportId) {
        problemService.deleteSolution(reportId);
        return Result.success();
    }
}