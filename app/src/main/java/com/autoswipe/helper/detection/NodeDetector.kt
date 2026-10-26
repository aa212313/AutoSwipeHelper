package com.autoswipe.helper.detection

import android.view.accessibility.AccessibilityNodeInfo
import com.autoswipe.helper.accessibility.AccessibilityConfig

/**
 * UI节点树检测器
 *
 * 红果短剧广告特征：
 * - 广告倒计时区域显示数字（如"5"、"4"..."0"）
 * - 底部区域显示"上滑继续观看短剧"
 * - 倒计时结束后文本保持不变，但页面状态改变
 *
 * 核心策略：只要在页面上找到"上滑继续观看短剧"相关文本就判定为广告结束
 */
class NodeDetector {

    /**
     * 检测广告是否已结束
     */
    fun detectAdFinished(rootNode: AccessibilityNodeInfo): Boolean {
        // 收集页面上所有可见文本
        val allVisibleTexts = collectVisibleTexts(rootNode)
        val fullPageText = allVisibleTexts.joinToString(" ")

        // 第一级：红果专属短语。这些短语本身就只在广告结束那一帧出现，命中即可判定
        for (pattern in AccessibilityConfig.HONGGUO_AD_FINISHED_PATTERNS) {
            if (fullPageText.contains(pattern)) {
                return true
            }
        }

        // 第二级：通用短语必须配上「广告标识」或「倒计时结束」才算数
        // 之前的问题出在这里：通用短语单独命中就返回 true，导致剧集正常播放时也被判定成广告结束
        val hasGeneralHint = AccessibilityConfig.GENERAL_AD_FINISHED_PATTERNS.any {
            fullPageText.contains(it)
        }
        if (!hasGeneralHint) return false

        val hasAdIndicator = AccessibilityConfig.AD_LABEL_PATTERNS.any {
            fullPageText.contains(it, ignoreCase = true)
        }
        if (hasAdIndicator) return true

        val hasCountdownEnd = AccessibilityConfig.COUNTDOWN_END_PATTERNS.any {
            fullPageText.contains(it)
        }
        return hasCountdownEnd
    }

    /**
     * 检测当前是否在广告中
     */
    fun isInAd(rootNode: AccessibilityNodeInfo): Boolean {
        val fullPageText = collectVisibleTexts(rootNode).joinToString(" ")

        // 有广告标识
        val hasAdLabel = AccessibilityConfig.AD_LABEL_PATTERNS.any { fullPageText.contains(it) }
        if (hasAdLabel) return true

        // 有滑动继续观看提示
        val hasSwipeHint = (AccessibilityConfig.HONGGUO_AD_FINISHED_PATTERNS +
                AccessibilityConfig.GENERAL_AD_FINISHED_PATTERNS).any { fullPageText.contains(it) }
        if (hasSwipeHint) return true

        return false
    }

    /**
     * 获取匹配文本（用于调试）
     */
    fun getMatchedTexts(rootNode: AccessibilityNodeInfo): List<String> {
        val fullPageText = collectVisibleTexts(rootNode).joinToString(" ")
        val allPatterns = AccessibilityConfig.HONGGUO_AD_FINISHED_PATTERNS +
                AccessibilityConfig.GENERAL_AD_FINISHED_PATTERNS +
                AccessibilityConfig.COUNTDOWN_END_PATTERNS +
                AccessibilityConfig.AD_LABEL_PATTERNS

        return allPatterns.filter { fullPageText.contains(it) }
    }

    /**
     * 收集页面上所有可见节点的文本
     */
    private fun collectVisibleTexts(node: AccessibilityNodeInfo): List<String> {
        val results = mutableListOf<String>()
        collectTextsRecursive(node, results)
        return results
    }

    private fun collectTextsRecursive(
        node: AccessibilityNodeInfo,
        results: MutableList<String>
    ) {
        // 收集节点的文本
        val text = node.text?.toString()?.trim()
        if (!text.isNullOrEmpty() && node.isVisibleToUser) {
            results.add(text)
        }

        // 收集内容描述
        val contentDesc = node.contentDescription?.toString()?.trim()
        if (!contentDesc.isNullOrEmpty()) {
            results.add(contentDesc)
        }

        // 递归子节点
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectTextsRecursive(child, results)
            child.recycle()
        }
    }
}