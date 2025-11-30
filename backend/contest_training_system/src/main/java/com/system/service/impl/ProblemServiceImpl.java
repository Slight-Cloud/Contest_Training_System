package com.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.system.dto.ProblemCreateDTO;
import com.system.dto.ProblemQueryDTO;
import com.system.dto.ProblemUpdateDTO;
import com.system.entity.Problem;
import com.system.entity.SortOrder;
import com.system.mapper.ProblemMapper;
import com.system.service.ProblemService;
import com.system.util.UserContext;
import com.system.vo.PageResultVO;
import com.system.vo.ProblemDetailVO;
import com.system.vo.ProblemListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.system.util.UserContext.getUserId;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private ProblemMapper problemMapper;

//    @Autowired
//    private ProblemDatasetMapper datasetMapper;

    @Override
    @Transactional // 题目创建后续可能涉及多表操作，保证一致性
    public Problem createProblem(ProblemCreateDTO createDTO) {

        Long creatorId = getUserId();
        Problem problem = new Problem();
        BeanUtils.copyProperties(createDTO, problem);
        problem.setCreatorId(creatorId);
        problemMapper.insertProblem(problem);

        // TODO: 创建题目的同时，应该把testdataZip信息存入ProblemDataset表
        // 这里暂时简化，只创建题目本身
        return problem;
    }

    @Override
    @Transactional
    public String updateProblem(Long problemId, ProblemUpdateDTO updateDTO) {
        Problem problem = problemMapper.findById(problemId);
        if (problem == null) {
            throw new RuntimeException("题目不存在");
        }
        BeanUtils.copyProperties(updateDTO, problem);
        problemMapper.updateProblem(problem);

        return "题目更新成功";
    }

    @Override
    @Transactional // 保证操作的原子性
    public String deleteOrHideProblem(Long problemId) {
        int usageCount = problemMapper.countUsageInContests(problemId);
        if (usageCount > 0) {
            problemMapper.hideProblemById(problemId);
            return "题目已在赛事中使用，已隐藏";
        }
        else {
            // 在实际项目中，删除前可能还需要删除关联的测试数据等
            problemMapper.deleteProblemById(problemId);
            return "题目删除成功";
        }
    }

    /**
     * @param queryDTO 包含所有查询参数的DTO
     * @return 封装好的分页结果VO
     */
    @Override
    public PageResultVO<ProblemListVO> getProblemList(ProblemQueryDTO queryDTO) {


        // 1. 设置分页参数。PageHelper的startPage方法是线程安全的。
        // 它会拦截接下来执行的第一个MyBatis查询，并自动在SQL后追加LIMIT分页。
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());

        //准备参数：
        //1.5对学生角色做区分，只能查看未隐藏题目
        String role = UserContext.getRole();
        if (!"ADMIN".equals(role) && !"TEACHER".equals(role)) {
            queryDTO.setIsHidden(0); // 学生只能查看未隐藏题目
        }
        //1.6处理排序参数
        String sortBy = queryDTO.getSortBy();
        SortOrder sortOrder = SortOrder.forProblem(sortBy);
        String sortColumn = sortOrder.getColumn();
        String sortDirection = sortOrder.getDirection();

        // 2. 执行查询。
        // 这里的 pageQuery 方法返回的虽然是 List<Problem>，
        // 但PageHelper通过AOP技术，在运行时实际返回的是它自己的 Page<Problem> 对象，
        List<ProblemListVO> problemListVO = problemMapper.pageQuery(queryDTO, sortColumn, sortDirection);

        // 3. 将查询结果转换为 PageInfo 对象，方便获取分页数据
        // 如果直接使用 List<Problem> 强转为 Page<Problem> 也可以
        Page<ProblemListVO> page = (Page<ProblemListVO>) problemListVO;

        // 5. 封装成自定义的 PageResultVO 返回给前端
        return new PageResultVO<>(page.getTotal(), page.getPageNum(), page.getPageSize(), page);
    }

    //题目详情查询
    @Override
    public ProblemDetailVO getProblemDetail(Long problemId) {

        Problem problem = problemMapper.findById(problemId);
        if (problem == null) {
            throw new RuntimeException("题目不存在");
        }
        ProblemDetailVO detailVO = new ProblemDetailVO();
        BeanUtils.copyProperties(problem, detailVO);

        return detailVO;

    }

}