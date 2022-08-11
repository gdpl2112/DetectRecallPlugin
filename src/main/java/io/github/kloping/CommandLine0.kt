package io.github.kloping

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.java.JCompositeCommand


/**
 *
 * @version 1.0
 * @author github kloping
 * @date 2022/8/8-9:59
 */
class CommandLine0 private constructor() : JCompositeCommand(DetectRecallPlugin.INSTANCE, "detectRecall") {
    companion object {
        @JvmField
        val INSTANCE = CommandLine0()
    }

    @Description("设置撤回消息监听最大延时时间")
    @SubCommand("setMil")
    suspend fun CommandSender.detectRecallPluginSetMil(@Name("分钟") int: Int) {
        DetectRecallPlugin.INSTANCE.configData.mil = int;
        DetectRecallPlugin.INSTANCE.configData.apply()
        sendMessage("消息监听时长${int}分钟")
    }

    @Description("添加撤回监听黑名单")
    @SubCommand("addBlack")
    suspend fun CommandSender.detectRecallPluginAddBlack(@Name("id") id: Long) {
        val configData = DetectRecallPlugin.INSTANCE.configData;
        if (!configData.blacklist.contains(id)) {
            configData.blacklist.add(id)
            configData.apply()
            sendMessage("成功添加${id}至撤回监听黑名单")
        } else {
            sendMessage("重复添加")
        }
    }

    @Description("移除撤回监听黑名单")
    @SubCommand("removeBlack")
    suspend fun CommandSender.detectRecallPluginRemoveBlack(@Name("id") id: Long) {
        val configData = DetectRecallPlugin.INSTANCE.configData;
        if (configData.blacklist.contains(id)) {
            configData.blacklist.remove(id)
            configData.apply()
            sendMessage("成功移除${id}从撤回监听黑名单")
        } else {
            sendMessage("从撤回监听黑名单未发现${id}")
        }
    }
}