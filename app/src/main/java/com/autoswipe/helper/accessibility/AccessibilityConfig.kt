package com.autoswipe.helper.accessibility

/**
 * 无障碍服务配置常量
 * 红果短剧专项优化
 */
object AccessibilityConfig {

    /** 目标应用包名 */
    val TARGET_PACKAGES = setOf(
        "com.phoenix.read",         // 红果免费短剧（字节，真实包名）
        "com.redfruit.drama",       // 历史记录：部分资料里这么写，实际不存在，留着无副作用
        "com.ss.android.ugc.aweme", // 抖音
        "com.kuaishou.nebula",      // 快手
    )

    /** 事件处理防抖间隔（毫秒） */
    const val EVENT_DEBOUNCE_MS = 300L

    /** 默认两次滑动最小间隔（毫秒） */
    const val DEFAULT_SWIPE_INTERVAL_MS = 2000L

    /** 默认检测到广告后延迟执行上滑（毫秒） */
    const val DEFAULT_SWIPE_DELAY_MS = 500L

    /** 默认滑动持续时间（毫秒） */
    const val DEFAULT_SWIPE_DURATION_MS = 300L

    /**
     * 红果短剧专属 - 广告结束标识文本
     *
     * 原则：只保留「只在广告结束那一帧才会出现」的完整短语。
     * 不再收录「短剧」「继续观看」这类单独成词的泛化词——
     * 红果首页/播放页到处都是「短剧」二字，收进来会导致疯狂误触。
     */
    val HONGGUO_AD_FINISHED_PATTERNS = listOf(
        "上滑继续观看短剧",
        "上滑继续观看",
        "上滑观看下一集",
        "向上滑动继续",
        "滑动继续观看",
    )

    /**
     * 通用广告结束标识文本
     *
     * 这些词单独出现不足以判定（正常播放页也可能有），
     * 必须配合 AD_LABEL_PATTERNS 或 COUNTDOWN_END_PATTERNS 才算命中。
     */
    val GENERAL_AD_FINISHED_PATTERNS = listOf(
        "上滑继续观看",
        "上滑观看",
        "滑动继续",
        "向上滑动",
        "观看下集",
        "跳过广告",
        "广告结束",
    )

    /**
     * 倒计时结束文本模式
     *
     * 注意：不要收录「播放中」——正常播放全程都是「播放中」，会持续误判。
     */
    val COUNTDOWN_END_PATTERNS = listOf(
        "0s", "0秒", "即将播放"
    )

    /**
     * 广告相关文本（辅助判断）
     *
     * 注意：不要收录裸的 "ad"——它是 "load"/"download"/"read" 的子串，
     * contains 匹配会大面积误命中。
     */
    val AD_LABEL_PATTERNS = listOf(
        "广告", "推广", "赞助", "sponsored", "广告倒计时"
    )
}
