package com.greendoyle.aihelpmegetjob.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast

object AppLauncher {

    /**
     * 外部只调用这一个方法 👇
     * 自动检查是否安装 + 自动跳转 + 自动提示
     */
    fun launchRecruitApp(context: Context, app: RecruitApp): Boolean {
        // 1. 先检查是否安装（正确方法）
        if (!isAppInstalled(context, app.packageName)) {
            Toast.makeText(context, "请先安装 ${app.appName}", Toast.LENGTH_SHORT).show()
            return false
        }

        // 2. 已安装 → 尝试启动
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
                true
            } else {
                Toast.makeText(context, "${app.appName} 无法启动", Toast.LENGTH_SHORT).show()
                false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "启动失败", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * 正确判断 APP 是否安装（供内部使用）
     */
    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}