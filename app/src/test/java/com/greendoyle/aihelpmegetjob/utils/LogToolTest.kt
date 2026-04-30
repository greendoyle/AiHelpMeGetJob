
package com.greendoyle.aihelpmegetjob.utils

import org.junit.Test
import org.junit.Assert.*

/**
 * LogTool 单元测试
 */
class LogToolTest {

    /**
     * 测试 maskApiKey 方法 - 正常长度 API Key
     */
    @Test
    fun maskApiKey_normalLength() {
        val apiKey = "abcdefghijklmnopqrstuvwxyz"
        val result = LogTool.maskApiKey(apiKey)
        assertEquals("abcd********************yz", result)
    }

    /**
     * 测试 maskApiKey 方法 - 短 API Key (8个字符)
     */
    @Test
    fun maskApiKey_eightCharacters() {
        val apiKey = "abcdefgh"
        val result = LogTool.maskApiKey(apiKey)
        assertEquals("********", result)
    }

    /**
     * 测试 maskApiKey 方法 - 短 API Key (少于8个字符)
     */
    @Test
    fun maskApiKey_lessThanEightCharacters() {
        val apiKey = "abcde"
        val result = LogTool.maskApiKey(apiKey)
        assertEquals("*****", result)
    }

    /**
     * 测试 maskApiKey 方法 - 空 API Key
     */
    @Test
    fun maskApiKey_emptyKey() {
        val apiKey = ""
        val result = LogTool.maskApiKey(apiKey)
        assertEquals("", result)
    }

    /**
     * 测试 maskApiKey 方法 - 长度超过16个字符的 API Key
     */
    @Test
    fun maskApiKey_longKey() {
        val apiKey = "abcdefghijklmnopqrstuvwxyz123456"
        val result = LogTool.maskApiKey(apiKey)
        assertEquals("abcd************************3456", result)
    }

    /**
     * 测试 maskApiKey 方法 - 正好16个字符的 API Key
     */
    @Test
    fun maskApiKey_exactlySixteenCharacters() {
        val apiKey = "abcdefghijklmnop"
        val result = LogTool.maskApiKey(apiKey)
        assertEquals("abcd********mnop", result)
    }
}
