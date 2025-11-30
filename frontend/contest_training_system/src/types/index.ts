// ==================== 用户相关类型 ====================

export interface User {
  userId: number;
  email: string;
  phoneNumber: string;
  nickname: string;
  studentId: string;
  role: UserRole;
  isActive: number;
  createdAt: string;
}

export type UserRole = 'ADMIN' | 'TEACHER' | 'JUDGE' | 'STUDENT';

export interface LoginPayload {
  emailOrPhone: string;
  password: string;
}

export interface RegisterPayload {
  email: string;
  phoneNumber: string;
  nickname: string;
  studentId: string;
  password: string;
}

export interface UpdateUserPayload {
  nickname?: string;
  phoneNumber?: string;
  password?: string;
}

// ==================== 赛事相关类型 ====================

export interface Contest {
  contestId: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  password?: string;
  visibility: 'PUBLIC' | 'PRIVATE';
  status: ContestStatus;
  creatorId: number;
  participantCount?: number;
  problems?: ContestProblem[];
  createdAt?: string;
}

export type ContestStatus = 'SCHEDULED' | 'ONGOING' | 'ENDED';

export interface ContestProblem {
  problemId: number;
  title: string;
  displayOrder: number;
  timeLimit: number;
  memoryLimit: number;
}

export interface ContestListParams {
  page?: number;
  pageSize?: number;
  keyword?: string;
  visibility?: string; // "PUBLIC" or "PRIVATE"
  status?: string;     // "SCHEDULED", "ONGOING", "ENDED"
  state?: string;      // "HIDDEN" or "USING" (admin/teacher only)
  sortBy?: string;     // e.g., "START_TIME_DESC", "created_desc"
}

export interface ContestPayload {
  contestId?: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  password?: string;
  visibility: 'PUBLIC' | 'PRIVATE';
  problemIds: number[];
}

export interface JoinContestPayload {
  contestId: number;
  password?: string;
  teamName?: string;
}

export interface HasJoinedContestPayload {
  contestId: number;
  password?: string;
  teamName?: string;
}

// ==================== 题目相关类型 ====================

export interface Problem {
  problemId: number;
  title: string;
  description: string;
  inputSpec: string;
  outputSpec: string;
  sampleInput: string;
  sampleOutput: string;
  remark?: string;
  timeLimit: number;
  memoryLimit: number;
  creatorId: number;
  isHidden: number;
  createdAt: string;
  datasets?: Dataset[];
}

export interface Dataset {
  datasetId: number;
  version: number;
  zipUrl: string;
  isActive: number;
  addedAt: string;
}

export interface ProblemListParams {
  page?: number;
  pageSize?: number;
  keyword?: string;
  creatorId?: number;
  isHidden?: number;
}

export interface ProblemPayload {
  problemId?: number;
  title: string;
  description: string;
  inputSpec: string;
  outputSpec: string;
  sampleInput: string;
  sampleOutput: string;
  remark?: string;
  timeLimit: number;
  memoryLimit: number;
  testdataZip?: string;
  isHidden?: number;
}

export interface ProblemDatasetPayload {
  problemId: number;
  testdataZip: string;
}

export interface SolutionReport {
  reportId: number;
  problemId: number;
  title: string;
  content: string;
  creatorId: number;
  creatorName?: string;
  isPublished: number;
  createdAt: string;
}

export interface SolutionReportPayload {
  problemId: number;
  title: string;
  content: string;
  isPublished: number;
}

// ==================== 提交相关类型 ====================

export interface Submission {
  submissionId: number;
  contestId: number;
  problemId: number;
  userId: number;
  nickname?: string;
  code: string;
  codeLength: number;
  language: string;
  compiler: string;
  result: SubmissionResult;
  timeUsed: number;
  memoryUsed: number;
  createdAt: string;
  judgeLogUrl?: string;
}

export type SubmissionResult = 'AC' | 'WA' | 'RE' | 'CE' | 'TE' | 'ME' | 'PE' | 'SE';

export interface SubmissionListParams {
  page?: number;
  pageSize?: number;
  contestId?: number;
  problemId?: number;
  userId?: number;
  result?: string;
  language?: string;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export interface SubmitCodePayload {
  contestId: number;
  problemId: number;
  code: string;
  language: string;
  compiler: string;
}

export interface TestCase {
  caseIndex: number;
  datasetVersion: number;
  result: SubmissionResult;
  timeUsed: number;
  memoryUsed: number;
  message?: string;
}

// ==================== 排名相关类型 ====================

export interface RankingItem {
  userId: number;
  nickname: string;
  rank?: number;
  solvedCount: number;
  penaltyMinutes: number;
  problemStats?: ProblemStat[];
}

export interface ProblemStat {
  problemId: number;
  displayOrder: number;
  isSolved: boolean;
  timeUsedMinutes: number;
  wrongAttempts: number;
  firstAcTime?: string;
}

// ==================== 训练计划相关类型 ====================

export interface TrainingPlan {
  planId: number;
  title: string;
  description?: string;
  status: TrainingPlanStatus;
  startTime: string;
  endTime: string;
  creatorId: number;
  studentCount: number;
  contestCount: number;
  contests?: TrainingPlanContest[];
  students?: TrainingPlanStudent[];
  createdAt?: string;
}

export type TrainingPlanStatus = 'SCHEDULE' | 'ONGOING' | 'ENDED';

export interface TrainingPlanContest {
  contestId: number;
  title: string;
  status: string;
  startTime: string;
  endTime: string;
  studentCount: number;
  contestCount: number;
}

export interface TrainingPlanStudent {
  userId: number;
  nickname: string;
  enrolledAt: string;
}

export interface TrainingPlanListParams {
  page?: number;
  pageSize?: number;
  state?: string;
  creatorId?: number;
  keyword?: string;
}

export interface TrainingPlanPayload {
  planId?: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  contestIds: number[];
  studentIds: number[];
}

// ==================== 通用响应类型 ====================

export interface ApiResponse<T = any> {
  code: number;
  msg?: string;
  data?: T;
}

export interface PaginationData<T> {
  total: number;
  page: number;
  pageSize: number;
  items: T[];
}

export interface ContestListData {
  total: number;
  page: number;
  pageSize: number;
  contests?: Contest[];
  list?: Contest[];  // 后端可能返回list
}

export interface ProblemListData {
  total: number;
  page: number;
  pageSize: number;
  problems?: Problem[];
  list?: Problem[];  // 后端实际返回的是list
}

export interface SubmissionListData {
  total: number;
  page: number;
  pageSize: number;
  submissions?: Submission[];
  list?: Submission[];  // 后端可能返回list
}

export interface UserListData {
  total: number;
  page: number;
  pageSize: number;
  users: User[];
}

export interface TrainingPlanListData {
  total: number;
  page: number;
  pageSize: number;
  plans: TrainingPlan[];
}

