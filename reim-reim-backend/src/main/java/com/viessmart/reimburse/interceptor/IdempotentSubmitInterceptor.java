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
 */
public class IdempotentSubmitInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只拦截 POST 请求
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从 URL 中提取 formUid（路径格式：/api/reimbursement/forms/{formUid}/submit）
        String path = request.getRequestURI();
        String formUid = extractFormUid(path);
        if (formUid == null) {
            return true;
        }

        // 生成幂等 key：formUid + 操作路径
        String idempotentKey = "form_submit:" + formUid + ":" + path;

        if (!IdempotentUtil.tryAcquire(idempotentKey)) {
            // 重复请求，返回提示
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);
            Result<String> result = Result.error("操作过于频繁，请勿重复提交", null);
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(result));
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后释放 key，允许后续正常操作
        String path = request.getRequestURI();
        String formUid = extractFormUid(path);
        if (formUid != null) {
            String idempotentKey = "form_submit:" + formUid + ":" + path;
            IdempotentUtil.release(idempotentKey);
        }
    }

    /**
     * 从 URL 路径中提取 formUid
     * 路径格式：/api/reimbursement/forms/{formUid}/submit 或 /cancel
     */
    private String extractFormUid(String path) {
        // 匹配 /api/reimbursement/forms/数字/submit 或 /cancel
        if (path.matches(".*/api/reimbursement/forms/\\d+/(submit|cancel|withdraw)$")) {
            String[] segments = path.split("/");
            // 倒数第二个是 formUid
            return segments[segments.length - 2];
        }
        return null;
    }
}
