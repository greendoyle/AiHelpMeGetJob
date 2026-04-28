## 1. 依赖引入

- [x] 1.1 在gradle/libs.versions.toml中添加版本定义：MMKV、OkHttp、Retrofit、Gson converter、Navigation Compose、kotlinx-coroutines
- [x] 1.2 在app/build.gradle.kts中添加依赖声明，启用kotlinx-serialization插件（可选）
- [x] 1.3 同步Gradle，确认所有依赖下载成功，项目可正常编译

## 2. 目录结构创建

- [x] 2.1 创建data/model/目录，定义ResumeInfo、AiConfig、FilterConfig、HistoryRecord四个data class
- [x] 2.2 创建mmkv/、network/、permission/目录
- [x] 2.3 创建ui/navigation/、ui/pages/目录

## 3. 页面导航（Page Navigation）

- [x] 3.1 创建ui/navigation/Screen.kt，定义sealed class路由（Home、Settings、AiSettings、Resume、History）
- [x] 3.2 创建ui/navigation/AppNavigation.kt，实现NavHost + 5Tab路由映射
- [x] 3.3 创建MainActivity.kt中的BottomNavigationBar组件（5个Tab：首页、参数配置、AI设置、简历录入、历史记录）
- [x] 3.4 集成NavHost与BottomNavigationBar，实现Tab切换联动

## 4. 5页面基础UI（Page Screens）

- [x] 4.1 创建ui/pages/HomePage.kt：任务状态展示区域（运行中/暂停/已完成）、已浏览/已打招呼计数、启动/暂停按钮
- [x] 4.2 创建ui/pages/SettingsPage.kt：筛选条件配置板块（关键词、城市、薪资、外包选项）+ 风控规则板块（操作间隔、每日限制），外层LazyColumn/Column + verticalScroll
- [x] 4.3 创建ui/pages/AiSettingsPage.kt：API URL输入框、API Key输入框（密码模式）、测试/保存/重置按钮
- [x] 4.4 创建ui/pages/ResumePage.kt：简历表单（姓名、求职意向、工作年限、学历、核心技能）+ 保存按钮，外层可滚动
- [x] 4.5 创建ui/pages/HistoryPage.kt：筛选条件区域（时间、分数范围）+ 空列表占位提示
- [x] 4.6 将5个页面Composable注册到AppNavigation路由中
- [x] 4.7 验收：编译运行，确认5个Tab正常显示、切换流畅、无布局错乱、无点击崩溃

## 5. MMKV存储封装（Storage Manager）

- [ ] 5.1 在Application初始化MMKV（在Application.onCreate中调用MMKV.initialize(context)）
- [ ] 5.2 创建mmkv/StorageManager.kt单例，封装基础类型get/set方法（String、Boolean、Int、Long）
- [ ] 5.3 封装复杂对象存储：通过Gson序列化data class为JSON字符串后存入MMKV
- [ ] 5.4 封装delete和clear方法
- [ ] 5.5 验收：存储简历信息后重启APP，确认数据正确读取

## 6. 网络请求封装（Network Client）

- [ ] 6.1 创建network/ApiClient.kt单例，配置OkHttp（超时时间30秒、添加LoggingInterceptor仅debug构建）
- [ ] 6.2 配置Retrofit实例：GsonConverterFactory、CoroutineCallAdapterFactory
- [ ] 6.3 创建OpenAI兼容API接口定义（POST /chat/completions，请求体含model/messages/temperature）
- [ ] 6.4 封装动态baseUrl功能（支持用户配置的API URL切换）
- [ ] 6.5 封装API连通性测试方法（轻量请求验证配置有效性）
- [ ] 6.6 验收：使用测试API URL确认能成功发起请求并解析返回结果

## 7. 权限引导与检测（Permission Helper）

- [ ] 7.1 创建permission/PermissionHelper.kt工具类
- [ ] 7.2 实现无障碍服务权限检测方法（遍历Settings.Secure检查服务是否开启）
- [ ] 7.3 实现悬浮窗权限检测方法（Settings.canDrawOverlays）
- [ ] 7.4 实现跳转无障碍服务设置页方法（Intent跳转ACTION_ACCESSIBILITY_SETTINGS）
- [ ] 7.5 实现跳转悬浮窗权限设置页方法（Intent跳转ACTION_MANAGE_OVERLAY_PERMISSION）
- [ ] 7.6 验收：APP启动检测权限状态，点击引导按钮能跳转至对应系统设置页

## 8. 集成验收

- [ ] 8.1 编译项目，确认无编译报错、无警告
- [ ] 8.2 安装到Android 10.0及以上真机/模拟器，确认启动无崩溃
- [ ] 8.3 全流程验收：5Tab切换正常、数据存储重启不丢失、API测试连通、权限引导跳转正常
