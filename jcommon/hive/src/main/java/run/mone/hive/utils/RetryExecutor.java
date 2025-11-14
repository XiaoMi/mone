package run.mone.hive.utils;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 通用重试执行器
 * 提供可配置的重试机制，支持中断、回调等功能
 *
 * @author goodjava@qq.com
 */
@Slf4j
public class RetryExecutor<T> {

    /**
     * 重试配置
     */
    @Data
    @Builder
    public static class RetryConfig {
        /**
         * 最大重试次数（默认3次）
         */
        @Builder.Default
        private int maxRetries = 3;

        /**
         * 中断标志检查器（可选）
         */
        private Supplier<Boolean> interruptChecker;

        /**
         * 重试前的回调（参数为当前重试次数）
         */
        private Consumer<Integer> onRetry;

        /**
         * 成功回调
         */
        private Consumer onSuccess;

        /**
         * 最终失败回调（所有重试都失败后）
         */
        private Consumer<Exception> onFinalFailure;

        /**
         * 中断回调
         */
        private Runnable onInterrupted;
    }

    private final RetryConfig config;

    public RetryExecutor(RetryConfig config) {
        this.config = config;
    }

    /**
     * 执行带重试的操作
     *
     * @param task        需要执行的任务
     * @param errorResult 失败时返回的默认结果
     * @return 执行结果
     */
    public T execute(Supplier<T> task, T errorResult) {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < config.maxRetries) {
            // 检查中断状态
            if (config.interruptChecker != null && config.interruptChecker.get()) {
                log.info("任务在第{}次重试前被中断", retryCount);
                if (config.onInterrupted != null) {
                    config.onInterrupted.run();
                }
                return errorResult;
            }

            try {
                // 如果不是第一次执行，触发重试回调
                if (retryCount > 0 && config.onRetry != null) {
                    config.onRetry.accept(retryCount);
                }

                // 执行任务
                T result = task.get();

                // 成功回调
                if (config.onSuccess != null) {
                    config.onSuccess.accept(result);
                }

                return result;

            } catch (Exception e) {
                lastException = e;
                retryCount++;
                log.error("任务执行失败(第{}次): {}", retryCount, e.getMessage(), e);

                // 如果已达到最大重试次数
                if (retryCount >= config.maxRetries) {
                    if (config.onFinalFailure != null) {
                        config.onFinalFailure.accept(e);
                    }
                    return errorResult;
                }
            }
        }

        return errorResult;
    }

    /**
     * 执行带重试的操作（使用 AtomicBoolean 作为错误标记）
     *
     * @param task     需要执行的任务
     * @param hasError 错误标记
     * @return 执行结果
     */
    public T executeWithErrorFlag(Supplier<T> task, T errorResult, AtomicBoolean hasError) {
        T result = execute(task, errorResult);
        if (result == errorResult && hasError != null) {
            hasError.set(true);
        }
        return result;
    }

    /**
     * 创建一个简单的重试执行器（只配置重试次数）
     *
     * @param maxRetries 最大重试次数
     * @return RetryExecutor实例
     */
    public static <T> RetryExecutor<T> simple(int maxRetries) {
        return new RetryExecutor<>(RetryConfig.builder()
                .maxRetries(maxRetries)
                .build());
    }

    /**
     * 创建一个支持中断的重试执行器
     *
     * @param maxRetries       最大重试次数
     * @param interruptChecker 中断检查器
     * @return RetryExecutor实例
     */
    public static <T> RetryExecutor<T> withInterrupt(int maxRetries, Supplier<Boolean> interruptChecker) {
        return new RetryExecutor<>(RetryConfig.builder()
                .maxRetries(maxRetries)
                .interruptChecker(interruptChecker)
                .build());
    }
}