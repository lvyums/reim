package com.viessmart.reimburse.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viessmart.reimburse.common.Result;
import com.viessmart.reimburse.tools.IdempotentUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 防重复提交拦截器
 * 基于 formUid + 操作路径 生成唯一 key，短时间内重复请求直接拒绝
 * key 在 3 秒后自动过期，不在 afterCompletion 中释放
 */
public class IdempotentSubmitInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只拦截 POST 请求
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从 URL 中提取 formUid
        String path = request.getRequestURI();
        String formUid = extractFormUid(path);
        if (formUid == null) {
            return true;
        }

        // 生成幂等 key：formUid + 操作类型
        String action = path.substring(path.lastIndexOf('/') + 1);
        String idempotentKey = "form_submit:" + formUid + ":" + action;

        if (!IdempotentUtil.tryAcquire(idempotentKey)) {
            // 重复请求，返回 200 + 业务错误码 429（与前端现有错误处理模式一致）
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);
            Result<String> result = Result.error(429, "操作过于频繁，请勿重复提交", null);
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(result));
            return false;
        }

        return true;
    }

    /**
     * 从 URL 路径中提取 formUid
     * 路径格式：/api/reimbursement/forms/{formUid}/submit|cancel|withdraw
     */
    private String extractFormUid(String path) {
        if (path.matches(".*/api/reimbursement/forms/\\d+/(submit|cancel|withdraw)$")) {
            String[] segments = path.split("/");
            return segments[segments.length - 2];
        }
        return null;
    }
}
