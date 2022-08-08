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
    public suspend fun CommandSender.detectRecallPlugin(@Name("分钟") int: Int) {
        ConfigData.mil = int;
        sendMessage("消息监听时长${int}分钟")
    }
}