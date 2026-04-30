package com.greendoyle.aihelpmegetjob.utils

import java.util.concurrent.Executors

/**
 * 全局后台串行调度器
 * 专门用于：日志打印、JSON序列化、MMKV存储、轻量耗时任务
 */
object AppDispatch {
    // 单线程池：任务排队执行，保证日志顺序不乱
    private val singleExecutor = Executors.newSingleThreadExecutor()

    // 丢到后台执行
    fun launchIO(block: () -> Unit) {
        singleExecutor.execute(block)
    }
}