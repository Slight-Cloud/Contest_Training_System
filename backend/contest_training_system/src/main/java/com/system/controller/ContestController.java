package com.system.controller;

import com.system.dto.ContestCreateDTO;
import com.system.dto.ContestJoinDTO;
import com.system.dto.ContestQueryDTO;
import com.system.dto.ContestUpdateDTO;
import com.system.service.ContestService;
import com.system.vo.ContestDetailVO;
import com.system.vo.PageResultVO;
import com.system.vo.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contest")
@Validated // 在类级别开启校验
public class ContestController {

    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    /**
     * 创建赛事 (仅限教师或管理员)
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Long> createContest(@RequestBody @Validated ContestCreateDTO contestCreateDTO) {
        Long contestId = contestService.createContest(contestCreateDTO);
        return Result.success(contestId);
    }

    /**
     * 更新赛事 (仅限教师或管理员)
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<?> updateContest(@RequestBody @Validated ContestUpdateDTO contestUpdateDTO) {
        contestService.updateContest(contestUpdateDTO);
        return Result.success();
    }

    /**
     * 删除赛事 (仅限教师或管理员)
     */
    @DeleteMapping("/{contestId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<?> deleteContest(@PathVariable Long contestId) {
        contestService.deleteContest(contestId);
        return Result.success();
    }

    /**
     * 查询赛事列表 (教师/管理员可见所有信息，学生仅可见公开的以及USING的赛事)
     */
    @GetMapping("/list")
    public Result<PageResultVO> getContestList(@Validated ContestQueryDTO contestQueryDTO) {
        PageResultVO pageResult = contestService.getContestList(contestQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取赛事详情 (公开)
     */
    @GetMapping("/{contestId}")
    public Result<ContestDetailVO> getContestDetail(
            @PathVariable Long contestId,
            @RequestParam(required = false) String password) {
        ContestDetailVO contestDetail = contestService.getContestDetail(contestId, password);
        return Result.success(contestDetail);
    }

    /**
     * 学生报名参赛
     */
    @PostMapping("/join")
    public Result<?> joinContest(@RequestBody @Validated ContestJoinDTO contestJoinDTO) {
        contestService.joinContest(contestJoinDTO);
        return Result.success();
    }

    @PostMapping("/hasJoined")
    public Result<Boolean> hasJoinedContest(@RequestBody @Validated ContestJoinDTO contestJoinDTO) {
        boolean hasJoined = contestService.hasJoinedContest(contestJoinDTO);
        return Result.success(hasJoined);
    }
}

