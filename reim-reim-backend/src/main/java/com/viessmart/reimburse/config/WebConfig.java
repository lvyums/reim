package com.viessmart.reimburse.config;

import com.viessmart.reimburse.interceptor.FormContextInterceptor;
import com.viessmart.reimburse.interceptor.IdempotentSubmitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 表单上下文清理拦截器
        registry.addInterceptor(new FormContextInterceptor())
                .addPathPatterns("/api/reimbursement/**");

        // 防重复提交拦截器（只拦截状态变更接口）
        registry.addInterceptor(new IdempotentSubmitInterceptor())
                .addPathPatterns("/api/reimbursement/forms/*/submit",
                        "/api/reimbursement/forms/*/cancel",
                        "/api/reimbursement/forms/*/withdraw");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
