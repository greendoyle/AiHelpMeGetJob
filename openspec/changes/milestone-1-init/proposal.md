## Why

MVP里程碑1需要完成工程初始化，将当前Compose空白模板项目升级为具备完整依赖、页面骨架、存储封装、网络封装和权限工具的可运行底座，为后续无障碍服务和AI链路开发提供基础。

## What Changes

- 引入核心依赖：MMKV（本地存储）、OkHttp + Retrofit + Gson（网络请求）、Navigation Compose（页面导航）、kotlinx-coroutines（异步）
- 创建BottomNavigation 5Tab页面架构：首页、参数配置页、AI模型设置页、简历录入页、历史记录页
- 封装MMKV统一存储管理类，定义数据模型（简历信息、AI配置、筛选条件等）
- 封装Retrofit网络请求工具类，支持OpenAI兼容API调用
- 封装权限检测与引导工具类（无障碍服务权限、悬浮窗权限）
- 改造MainActivity为导航容器，串联5个页面

## Capabilities

### New Capabilities
- `storage-manager`: MMKV统一存储封装，提供存/取/删/清空操作，支撑简历、配置、历史等数据的本地持久化
- `network-client`: OkHttp + Retrofit网络请求封装，支持OpenAI兼容API调用和JSON解析
- `permission-helper`: 权限检测与系统设置引导工具，覆盖无障碍服务权限和悬浮窗权限
- `page-navigation`: Compose Navigation页面导航架构，BottomNavigation 5Tab路由
- `page-screens`: 5个页面基础UI布局（首页、参数配置页、AI模型设置页、简历录入页、历史记录页）

### Modified Capabilities
- None（新项目，无已有能力需求变更）

## Impact

- 新增依赖：MMKV、OkHttp 4.x、Retrofit 2.x、Gson、Navigation Compose、kotlinx-coroutines
- 修改文件：`app/build.gradle.kts`（依赖）、`gradle/libs.versions.toml`（版本目录）、`AndroidManifest.xml`（权限声明）、`MainActivity.kt`（导航容器）
- 新增目录：`mmkv/`、`network/`、`permission/`、`ui/pages/`、`ui/navigation/`、`data/model/`
- 不改动现有Theme文件（Color.kt、Theme.kt、Type.kt可复用）
