## ADDED Requirements

### Requirement: OpenAI兼容API调用
系统 SHALL 通过Retrofit封装提供POST方法，支持调用OpenAI兼容格式的聊天接口。

#### Scenario: 成功调用API
- **WHEN** 用户传入model、messages、temperature等参数发起请求
- **THEN** 系统返回AI生成的文本内容

#### Scenario: API调用失败
- **WHEN** API返回错误状态码（如401、500）
- **THEN** 系统返回明确的错误信息，不崩溃

### Requirement: 动态API配置
系统 SHALL 支持根据用户配置的API URL和API Key动态发起请求。

#### Scenario: 使用用户配置发起请求
- **WHEN** 用户在AI设置页填写API URL和Key后调用API
- **THEN** 请求携带正确的Authorization Bearer Token

#### Scenario: API URL变更
- **WHEN** 用户修改API URL配置
- **THEN** 后续请求使用新的URL，不缓存旧配置

### Requirement: 异常处理
系统 SHALL 处理网络超时、连接中断等异常场景，返回明确的错误回调。

#### Scenario: 网络超时
- **WHEN** API请求超时（默认30秒）
- **THEN** 系统返回超时错误，不挂起线程

#### Scenario: 网络中断
- **WHEN** 用户设备无网络连接
- **THEN** 系统返回网络不可达错误

### Requirement: API连通性测试
系统 SHALL 提供独立的API连通性测试方法，用于验证用户填写的API配置是否有效。

#### Scenario: API连通性测试成功
- **WHEN** 用户点击"测试API"按钮，API URL和Key有效
- **THEN** 系统返回成功提示

#### Scenario: API连通性测试失败
- **WHEN** 用户点击"测试API"按钮，API URL或Key无效
- **THEN** 系统返回失败原因（如认证失败、URL不可达）
