package com.viessmart.reimburse.interceptor;

import com.viessmart.reimburse.tools.FormIdContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器：作废报销单自动清空 ThreadLocal
 */
public class FormContextInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        FormIdContext.remove();
    }
}
