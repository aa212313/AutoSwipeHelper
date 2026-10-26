# 自动上滑助手 (AutoSwipeHelper)

一款 Android 原生应用，利用无障碍服务在短视频广告倒计时结束后**自动上滑**，无需手动操作即可继续观看短剧。

---

## 痛点

免费短视频 App（如红果短剧）在播放过程中会弹出倒计时广告，底部显示「上滑继续观看短剧」。用户必须等倒计时结束再手动上滑。本应用帮你**自动完成这个操作**。


## 截图
![02-settings.png](模拟截图)

## 工作原理

```
出现广告 → 倒计时 → 检测到"上滑继续观看"文本 → 模拟上滑手势 → 继续观看
```

通过 Android **AccessibilityService** 读取屏幕 UI 节点，检测到广告结束标识文本后，自动执行上滑手势。

## 功能

- 🔍 **智能检测**：通过 UI 节点文本识别广告结束状态（支持"上滑继续观看短剧"等关键词）
- 👆 **自动上滑**：模拟真实手指滑动（按下 → 滑动 → 抬起），滑完自动释放
- 🎯 **红果短剧专项优化**：针对红果短剧的广告 UI 做了专门适配
- 🎛️ **悬浮窗控制**：可拖拽悬浮球，点击切换启用/暂停，双击打开主界面
- 📢 **通知栏状态**：状态栏显示服务运行状态和上滑次数
- 🔋 **服务保活**：前台服务 + 电池优化豁免，防止被系统杀死
- ⚙️ **参数可调**：滑动延迟、滑动速度、最小间隔等均可自定义

## 使用步骤

1. 安装 APK
2. 打开应用，点击「开启无障碍服务」
3. 在系统设置中找到「自动上滑助手」并开启
4. 授权悬浮窗权限和通知权限
5. 返回应用，点击「▶ 开始」
6. 打开红果短剧，享受自动跳过广告

## 目标应用

| 应用 | 包名 | 状态 |
|------|------|------|
| 红果短剧 | `com.redfruit.drama` | ✅ 专项适配 |
| 抖音 | `com.ss.android.ugc.aweme` | 🚧 通用适配 |
| 快手 | `com.kuaishou.nebula` | 🚧 通用适配 |

## 系统要求

- Android 8.0+ (API 26)
- 需要开启无障碍服务
- 需要悬浮窗权限
- 建议关闭电池优化

## 项目结构

```
app/src/main/java/com/autoswipe/helper/
├── App.kt                              # Application 入口
├── accessibility/
│   ├── AutoSwipeService.kt             # 核心无障碍服务
│   └── AccessibilityConfig.kt          # 配置常量
├── detection/
│   ├── AdDetector.kt                   # 广告检测调度器
│   ├── NodeDetector.kt                 # UI 节点文本检测
│   ├── CountdownDetector.kt            # 倒计时数字检测
│   ├── TimerBasedDetector.kt           # 固定计时兜底
│   └── ScreenState.kt                  # 状态定义
├── gesture/
│   ├── GestureController.kt            # 手势模拟
│   └── GestureConfig.kt                # 手势参数
├── config/
│   ├── PreferencesManager.kt           # 配置持久化
│   └── AppConfig.kt                    # 应用常量
├── permission/
│   └── PermissionHelper.kt             # 权限管理
├── keepalive/
│   ├── KeepAliveForegroundService.kt   # 前台服务保活
│   └── BootReceiver.kt                 # 开机自启
├── ui/
│   ├── MainActivity.kt                 # 主界面
│   ├── SettingsActivity.kt             # 设置界面
│   └── FloatingWindowService.kt        # 悬浮窗
└── util/
    ├── NotificationHelper.kt           # 通知管理
    └── LogUtil.kt                      # 日志工具
```

## 技术栈

| 类别 | 选型 |
|------|------|
| 语言 | Kotlin |
| 最低 SDK | API 26 (Android 8.0) |
| 目标 SDK | API 34 (Android 14) |
| UI | ViewBinding + Material Design 3 |
| 核心 API | AccessibilityService.dispatchGesture() |
| 构建 | Gradle Kotlin DSL, AGP 8.5.0 |

## 构建

用 Android Studio 打开项目目录，Sync Gradle 后即可编译运行。

```bash
# 命令行构建
./gradlew assembleDebug
```

## 免责声明

本应用仅供个人学习和技术研究使用。自动操作可能违反部分 App 的用户协议，请自行评估使用风险。开发者不承担因使用本应用产生的任何责任。

## License

MIT

---

## 关于本仓库（fork）

- **上游项目**：https://github.com/zabio/AutoSwipeHelper —— 原作者 zabio，MIT 许可
- 本仓库在上游基础上做了少量修正，**保留原作者版权与 MIT 许可**，未改变用途
- 与上游的差异见下方

## 相对上游的改动（2026-10-26）

**1. 修正广告判定关键词（`AccessibilityConfig.kt`）**

原版关键词表里混入了 `"短剧"`、`"滑动"`、`"下一集"`、`"播放中"` 以及裸的 `"ad"`。而 `detectAdFinished()` 用的是 `contains` 匹配，只要页面出现「短剧」二字就判定为广告结束并立即上滑——红果正常播放页满屏都是这两个字，导致持续误触。

现已删除全部泛化词，只保留「上滑继续观看短剧」这类**只在广告结束那一帧才出现**的完整短语；`"ad"` 一并移除（它是 `load` / `download` 的子串）。

**2. 收紧判定逻辑（`NodeDetector.kt`）**

通用短语不再单独命中即返回，必须同时具备广告标识（广告 / 推广 / 赞助）或倒计时结束；同时移除「页面出现 `0s` 就判定广告结束」的分支。

**3. 无障碍服务限定目标应用（`AutoSwipeService.kt`）**

原版不设置 `packageNames`，等于在系统层面监听全机所有应用。现改为在 `onServiceConnected` 时从偏好设置动态下发目标包名，并新增 `refreshPackageFilter()` 供目标列表变化后刷新。

**4. 修正包名注释**

红果短剧的真实包名是 `com.phoenix.read`；`com.redfruit.drama` 并不存在（保留在集合内无副作用）。
