package io.github.kloping

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

/**
 * @author github.kloping
 */
object ConfigData : AutoSavePluginConfig("DetectRecallConfig") {
    var mil: Int by value(5)
    val blacklist: java.util.ArrayList<Long> by value(ArrayList<Long>());
}