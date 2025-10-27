package com.system.service;

import com.system.dto.ProblemCreateDTO;
import com.system.dto.ProblemQueryDTO;
import com.system.dto.ProblemUpdateDTO;
import com.system.entity.Problem;
import com.system.vo.PageResultVO;
import com.system.vo.ProblemDetailVO;
import com.system.vo.ProblemListVO;

// ...
public interface ProblemService {

    Problem createProblem(ProblemCreateDTO createDTO);

    String updateProblem(Long problemId, ProblemUpdateDTO updateDTO);

    String deleteOrHideProblem(Long problemId);

    PageResultVO<ProblemListVO> getProblemList(ProblemQueryDTO queryDTO);

    ProblemDetailVO getProblemDetail(Long problemId);
}