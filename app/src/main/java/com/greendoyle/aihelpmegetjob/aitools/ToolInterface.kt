package com.greendoyle.aihelpmegetjob.aitools

/**
 * 工具基类
 * 所有工具必须继承此抽象类
 */
abstract class Tool {
    /** 工具名称（抽象属性，必须实现） */
    abstract val name: String

    /** 工具描述（抽象属性，必须实现） */
    abstract val description: String

    /** 工具参数定义（OpenAI function calling 格式） */
    abstract val parameters: Map<String, Any>

    /**
     * 执行工具
     */
    abstract suspend fun execute(kwargs: Map<String, Any?>): String
}

/**
 * 工具注册中心
 */
object ToolRegistry {
    // 内部工具存储：name -> Tool
    private val _tools = mutableMapOf<String, Tool>()

    /** 注册工具 */
    fun register(tool: Tool) {
        _tools[tool.name] = tool
    }

    /** 根据名称获取工具 */
    fun get(name: String): Tool? = _tools[name]

    /** 获取所有工具列表 */
    fun listTools(): List<Tool> = _tools.values.toList()

    /**
     * 返回 OpenAI API 兼容的工具 Schema
     */
    fun getOpenaiToolsSchema(): List<Map<String, Any>> {
        return _tools.values.map { it.parameters }
    }
}