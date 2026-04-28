## Context

当前项目是Android Studio生成的Compose空白模板，仅包含基础主题和Hello World页面。Milestone 1需要在不改变项目基础框架的前提下，引入核心依赖、搭建页面骨架、封装存储/网络/权限工具类，形成可独立开发、可逐步验收的工程底座。

技术约束：
- 使用Jetpack Compose + Material3 UI框架
- Compose Navigation实现BottomNavigation 5Tab架构
- Gson作为Retrofit JSON解析方案
- MMKV作为唯一存储介质（轻量KV场景）
- 最低支持Android 10.0（API 29）
- 简单MVP架构，页面与业务逻辑分离，不过度设计

## Goals / Non-Goals

**Goals:**
- 所有核心依赖正确引入，项目可编译打包
- 5个页面基础UI完整，BottomNavigation切换正常
- MMKV存储封装可用，支持结构化和简单配置数据的持久化
- 网络请求封装可用，能调通OpenAI兼容API
- 权限检测与引导可用，能跳转到对应系统设置页
- 目录结构清晰，后续里程碑可按目录定位开发

**Non-Goals:**
- 无障碍服务实现（里程碑2）
- AI匹配与话术生成（里程碑3）
- 历史记录数据填充与列表展示（里程碑4）
- 悬浮窗UI实现
- 业务逻辑填充（页面仅做基础布局，不涉及ViewModel/状态管理等复杂逻辑）

## Decisions

### 1. 页面导航架构：BottomNavigation 5Tab
- 选择BottomNavigation（5个Tab）而非首页卡片跳转
- 首页作为Tab之一，用户可随时回到首页查看任务状态
- 使用Compose Navigation的NavHost + BottomNavigationBar，通过sealed class定义路由

### 2. MMKV封装：StorageManager单例
- 不引入DI框架（Hilt/Koin），MVP阶段使用单例模式
- 复杂数据（如简历信息、历史记录）通过Gson序列化为JSON字符串后存入MMKV
- 提供get/set/delete/clear基础方法，按数据类型分命名空间
- 数据模型使用Kotlin data class

### 3. 网络请求：Retrofit + OkHttp + Gson
- OkHttp添加LoggingInterceptor（仅debug构建），方便开发调试
- Retrofit使用suspend函数（kotlinx-coroutines adapter），避免callback地狱
- 基础URL由用户配置（AI模型设置页），通过OkHttp动态baseUrl实现
- ApiClient单例管理Retrofit实例

### 4. 权限工具：PermissionHelper
- 使用ActivityResultLauncher替代已废弃的`onRequestPermissionsResult`
- 无障碍权限检测通过`isAccessibilitySettingsOn`方法（遍历`Settings.Secure`）
- 悬浮窗权限检测通过`Settings.canDrawOverlays`
- 跳转系统设置页使用Intent，封装统一方法

### 5. 目录结构
```
app/src/main/java/com/greendoyle/aihelpmegetjob/
├── MainActivity.kt            # 入口 → 导航容器
├── data/model/                # 数据模型
│   ├── ResumeInfo.kt
│   ├── AiConfig.kt
│   ├── FilterConfig.kt
│   └── HistoryRecord.kt
├── mmkv/
│   └── StorageManager.kt
├── network/
│   └── ApiClient.kt
├── permission/
│   └── PermissionHelper.kt
├── ui/
│   ├── theme/                 # 已有：Color.kt, Theme.kt, Type.kt
│   ├── navigation/
│   │   ├── AppNavigation.kt   # NavHost + 路由定义
│   │   └── Screen.kt          # Screen sealed class
│   └── pages/
│       ├── HomePage.kt
│       ├── SettingsPage.kt
│       ├── AiSettingsPage.kt
│       ├── ResumePage.kt
│       └── HistoryPage.kt
```

## Risks / Trade-offs

- **MMKV对复杂对象支持有限** → 通过Gson序列化data class为JSON字符串解决，MVP数据量小，性能影响可忽略
- **Compose Navigation与BottomNavigation的back栈处理** → 每个Tab维护独立back stack，首页作为根路由
- **OkHttp动态baseUrl在Retrofit中的实现** → 使用`@HttpUrl`参数或OkHttp Interceptor动态修改URL，需要额外封装
- **无障碍权限检测在不同厂商ROM上可能不一致** → MVP使用标准Android API检测，厂商适配在后续里程碑处理
