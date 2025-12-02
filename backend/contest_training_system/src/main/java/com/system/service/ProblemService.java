package com.system.service;

import com.system.dto.ProblemCreateDTO;
import com.system.dto.ProblemQueryDTO;
import com.system.dto.ProblemUpdateDTO;
import com.system.dto.SolutionCreateDTO;
import com.system.dto.SolutionUpdateDTO;
import com.system.entity.Problem;
import com.system.vo.PageResultVO;
import com.system.vo.ProblemDetailVO;
import com.system.vo.ProblemListVO;
import com.system.vo.SolutionVO;

// ...
public interface ProblemService {

    Problem createProblem(ProblemCreateDTO createDTO);

    String updateProblem(Long problemId, ProblemUpdateDTO updateDTO);

    String deleteOrHideProblem(Long problemId);

    PageResultVO<ProblemListVO> getProblemList(ProblemQueryDTO queryDTO);

    ProblemDetailVO getProblemDetail(Long problemId);

    void publishSolution(SolutionCreateDTO createDTO);

    PageResultVO<SolutionVO> getSolutionList(Long problemId, int page, int pageSize);

    SolutionVO getSolutionDetail(Long reportId);

    void updateSolution(SolutionUpdateDTO updateDTO);

    void deleteSolution(Long reportId);
}