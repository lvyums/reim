package com.viessmart.reimburse.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 幂等校验工具类（基于本地内存，适用于单机部署场景）
 * 用 ConcurrentHashMap 存储请求标识，ScheduledExecutorService 定时清理过期 key
 */
public class IdempotentUtil {

    /**
     * 存储请求标识：key=请求标识, value=过期时间戳
     */
    private static final ConcurrentHashMap<String, Long> REQUEST_MAP = new ConcurrentHashMap<>();

    /**
     * 默认过期时间：3秒（防止短时间内重复提交）
     */
    private static final long DEFAULT_EXPIRE_MS = 3000;

    /**
     * 定时清理任务（每10秒清理一次过期 key）
     */
    private static final ScheduledExecutorService CLEANER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "idempotent-cleaner");
        t.setDaemon(true);
        return t;
    });

    static {
        CLEANER.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            REQUEST_MAP.entrySet().removeIf(entry -> entry.getValue() < now);
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取执行权（如果 key 已存在则返回 false）
     *
     * @param key 请求唯一标识
     * @return true=首次请求可以执行, false=重复请求拒绝执行
     */
    public static boolean tryAcquire(String key) {
        return tryAcquire(key, DEFAULT_EXPIRE_MS);
    }

    /**
     * 尝试获取执行权（自定义过期时间）
     *
     * @param key      请求唯一标识
     * @param expireMs 过期时间（毫秒）
     * @return true=首次请求可以执行, false=重复请求拒绝执行
     */
    public static boolean tryAcquire(String key, long expireMs) {
        Long previous = REQUEST_MAP.putIfAbsent(key, System.currentTimeMillis() + expireMs);
        return previous == null;
    }

    /**
     * 释放执行权（手动移除 key，用于执行完成后清理）
     *
     * @param key 请求唯一标识
     */
    public static void release(String key) {
        REQUEST_MAP.remove(key);
    }
}
