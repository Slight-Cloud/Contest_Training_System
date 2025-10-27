/**
 * 格式化日期时间字符串
 * @param dateString ISO格式的日期字符串
 * @returns 格式化后的日期字符串 YYYY-MM-DD HH:mm:ss
 */
export function formatDateTime(dateString?: string): string {
  if (!dateString) return '-';
  const normalized = dateString.trim();
  const formatted = normalized
    .replace('T', ' ')
    .replace(/\.\d+(Z|[+-]\d{2}:\d{2})?$/, '')
    .replace(/(Z|[+-]\d{2}:\d{2})$/, '');

  if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(formatted)) {
    return formatted;
  }

  const parsed = Date.parse(normalized);
  if (!Number.isNaN(parsed)) {
    const date = new Date(parsed);
    const pad = (value: number) => value.toString().padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ` +
           `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
  }

  return formatted;
}

/**
 * 格式化相对时间
 * @param dateString ISO格式的日期字符串
 * @returns 相对时间描述
 */
export function formatRelativeTime(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const week = 7 * day;
  const month = 30 * day;
  
  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`;
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`;
  } else if (diff < week) {
    return `${Math.floor(diff / day)}天前`;
  } else if (diff < month) {
    return `${Math.floor(diff / week)}周前`;
  } else {
    return formatDateTime(dateString);
  }
}

/**
 * 解析题目ID字符串
 * @param input 逗号分隔的ID字符串
 * @returns ID数组
 */
export function parseProblemIds(input: string): number[] {
  if (!input) return [];
  return input
    .split(',')
    .map((item) => Number(item.trim()))
    .filter((num) => !Number.isNaN(num) && num > 0);
}

/**
 * 判断赛事状态
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @returns 赛事状态
 */
export function getContestState(startTime?: string, endTime?: string): 'UPCOMING' | 'ONGOING' | 'FINISHED' {
  if (!startTime || !endTime) return 'UPCOMING';
  
  const now = Date.now();
  const start = Date.parse(startTime);
  const end = Date.parse(endTime);
  
  if (!Number.isNaN(start) && now < start) return 'UPCOMING';
  if (!Number.isNaN(end) && now > end) return 'FINISHED';
  return 'ONGOING';
}

/**
 * 获取状态标签类型
 * @param status 状态值
 * @returns Element Plus 标签类型
 */
export function getStatusTagType(status?: string): 'info' | 'success' | 'warning' | 'danger' {
  const statusMap: Record<string, 'info' | 'success' | 'warning' | 'danger'> = {
    UPCOMING: 'info',
    ONGOING: 'success',
    FINISHED: 'warning',
    AC: 'success',
    WA: 'danger',
    RE: 'danger',
    CE: 'warning',
    TE: 'warning',
    ME: 'warning',
  };
  return statusMap[status || ''] || 'info';
}

/**
 * 获取状态标签文本
 * @param status 状态值
 * @param type 状态类型
 * @returns 状态文本
 */
export function getStatusLabel(status?: string, type: 'contest' | 'submission' = 'contest'): string {
  if (type === 'contest') {
    const contestMap: Record<string, string> = {
      UPCOMING: '未开始',
      ONGOING: '进行中',
      FINISHED: '已结束',
    };
    return contestMap[status || ''] || '未知';
  } else {
    const submissionMap: Record<string, string> = {
      AC: '通过',
      WA: '答案错误',
      RE: '运行错误',
      CE: '编译错误',
      TE: '超时',
      ME: '内存超限',
      PE: '格式错误',
      SE: '系统错误',
    };
    return submissionMap[status || ''] || status || '未知';
  }
}

/**
 * 获取可见性标签文本
 * @param visibility 可见性值
 * @returns 可见性文本
 */
export function getVisibilityLabel(visibility?: string): string {
  const map: Record<string, string> = {
    PUBLIC: '公开',
    PRIVATE: '私有',
  };
  return map[visibility || ''] || '未知';
}

/**
 * 获取角色标签文本
 * @param role 角色值
 * @returns 角色文本
 */
export function getRoleLabel(role?: string): string {
  const map: Record<string, string> = {
    ADMIN: '管理员',
    TEACHER: '教师',
    JUDGE: '评委',
    STUDENT: '学生',
  };
  return map[role || ''] || '访客';
}

/**
 * 验证时间范围
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @returns 是否有效
 */
export function validateTimeRange(startTime: string, endTime: string): boolean {
  const startMs = Date.parse(startTime.replace(' ', 'T'));
  const endMs = Date.parse(endTime.replace(' ', 'T'));
  return !Number.isNaN(startMs) && !Number.isNaN(endMs) && endMs > startMs;
}

/**
 * 防抖函数
 * @param fn 要防抖的函数
 * @param delay 延迟时间(ms)
 * @returns 防抖后的函数
 */
export function debounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): (...args: Parameters<T>) => void {
  let timeoutId: ReturnType<typeof setTimeout> | null = null;
  
  return function (this: any, ...args: Parameters<T>) {
    if (timeoutId) {
      clearTimeout(timeoutId);
    }
    timeoutId = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

/**
 * 节流函数
 * @param fn 要节流的函数
 * @param delay 延迟时间(ms)
 * @returns 节流后的函数
 */
export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): (...args: Parameters<T>) => void {
  let lastCall = 0;
  
  return function (this: any, ...args: Parameters<T>) {
    const now = Date.now();
    if (now - lastCall >= delay) {
      lastCall = now;
      fn.apply(this, args);
    }
  };
}

/**
 * 深拷贝对象
 * @param obj 要拷贝的对象
 * @returns 拷贝后的对象
 */
export function deepClone<T>(obj: T): T {
  if (obj === null || typeof obj !== 'object') {
    return obj;
  }
  
  if (obj instanceof Date) {
    return new Date(obj.getTime()) as any;
  }
  
  if (obj instanceof Array) {
    return obj.map((item) => deepClone(item)) as any;
  }
  
  if (obj instanceof Object) {
    const clonedObj: any = {};
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key]);
      }
    }
    return clonedObj;
  }
  
  return obj;
}

