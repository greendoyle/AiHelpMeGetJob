## ADDED Requirements

### Requirement: 无障碍服务权限检测
系统 SHALL 提供检测方法，判断用户是否已在系统设置中开启无障碍服务权限。

#### Scenario: 无障碍权限已开启
- **WHEN** 用户在系统设置中已开启本工具的无障碍服务
- **THEN** PermissionHelper返回无障碍权限已开启

#### Scenario: 无障碍权限未开启
- **WHEN** 用户未在系统设置中开启本工具的无障碍服务
- **THEN** PermissionHelper返回无障碍权限未开启

### Requirement: 悬浮窗权限检测
系统 SHALL 提供检测方法，判断APP是否已获得悬浮窗权限。

#### Scenario: 悬浮窗权限已开启
- **WHEN** 用户已授权本工具悬浮窗权限
- **THEN** PermissionHelper返回悬浮窗权限已开启

#### Scenario: 悬浮窗权限未开启
- **WHEN** 用户未授权本工具悬浮窗权限
- **THEN** PermissionHelper返回悬浮窗权限未开启

### Requirement: 系统设置跳转
系统 SHALL 提供跳转到无障碍服务设置页和悬浮窗权限设置页的方法。

#### Scenario: 跳转至无障碍设置页
- **WHEN** 用户点击"开启无障碍服务"引导按钮
- **THEN** 系统跳转至无障碍服务设置页

#### Scenario: 跳转至悬浮窗设置页
- **WHEN** 用户点击"开启悬浮窗"引导按钮
- **THEN** 系统跳转至悬浮窗权限管理页

### Requirement: 权限状态变更通知
系统 SHALL 在用户从设置页返回后能重新检测权限状态。

#### Scenario: 返回后重新检测
- **WHEN** 用户在设置页开启权限后返回APP
- **THEN** 页面能重新检测并显示更新后的权限状态
