## ADDED Requirements

### Requirement: 数据存取
系统 SHALL 提供统一的MMKV存储管理类StorageManager，支持字符串、布尔、整型、长整型等基础类型数据的存取。

#### Scenario: 存储字符串数据
- **WHEN** 用户调用StorageManager设置键值对（key="api_url", value="https://api.example.com"）
- **THEN** 数据成功写入MMKV，读取时返回相同字符串

#### Scenario: 读取不存在的数据
- **WHEN** 用户调用StorageManager读取未设置的key
- **THEN** 返回null或默认值，不抛异常

### Requirement: 复杂对象存储
系统 SHALL 支持通过Gson序列化data class为JSON字符串后存入MMKV，读取时自动反序列化。

#### Scenario: 存储和读取简历信息
- **WHEN** 用户将ResumeInfo data class存入StorageManager
- **THEN** 读取时返回与原对象字段一致的ResumeInfo实例

#### Scenario: 存储筛选条件
- **WHEN** 用户将FilterConfig data class存入StorageManager
- **THEN** 读取时返回与原对象字段一致的FilterConfig实例

### Requirement: 数据删除
系统 SHALL 提供删除单个key和清空所有数据的方法。

#### Scenario: 删除单个数据
- **WHEN** 用户调用StorageManager删除指定key
- **THEN** 该key对应的数据被清除，后续读取返回null

#### Scenario: 清空所有数据
- **WHEN** 用户调用StorageManager清空所有数据
- **THEN** 所有存储的key-value对均被清除，存储恢复为空状态

### Requirement: 数据持久化
系统 SHALL 确保存储的数据在APP重启后可正常读取，不丢失。

#### Scenario: 重启后数据保留
- **WHEN** 用户存储简历信息后关闭并重启APP
- **THEN** 重新读取简历信息时，数据与存储时一致
