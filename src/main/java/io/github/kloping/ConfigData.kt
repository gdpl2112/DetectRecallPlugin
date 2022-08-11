package io.github.kloping

import io.github.kloping.file.FileUtils
import io.github.kloping.serialize.HMLObject
import java.io.File

/**
 * @author github.kloping
 */
data class ConfigData(
    var mil: Int = 5,
    val blacklist: HashSet<Long> = HashSet<Long>(),
) {
    fun apply() {
        FileUtils.putStringInFile(HMLObject.toHMLString(this), File(DetectRecallPlugin.CONFIG_PATH))
    }
}

