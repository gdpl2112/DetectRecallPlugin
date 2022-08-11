package io.github.kloping;

import io.github.kloping.file.FileUtils;
import io.github.kloping.serialize.HMLObject;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.permission.Permission;
import net.mamoe.mirai.console.permission.PermissionId;
import net.mamoe.mirai.console.permission.PermissionRegistryConflictException;
import net.mamoe.mirai.console.permission.PermissionService;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author github.kloping
 */
public class DetectRecallPlugin extends JavaPlugin {
    public static final DetectRecallPlugin INSTANCE = new DetectRecallPlugin();
    public static final String CONFIG_PATH = new File(DetectRecallPlugin.INSTANCE.getConfigFolderPath().toFile().getPath(), "conf.hml").getPath();
    public ConfigData configData = new ConfigData();

    public DetectRecallPlugin() {
        super(new JvmPluginDescriptionBuilder("io.github.kloping.DetectRecallPlugin", "1.1").info("监控撤回闪照").build());
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage storage) {
        super.onLoad(storage);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public PermissionId receiver = null;
    public PermissionId monitor = null;
    public Permission receiverPerm = null;
    public Permission monitorPerm = null;

    @Override
    public void onEnable() {
        super.onEnable();
        HMLObject object = HMLObject.parseObject(FileUtils.getStringFromFile(CONFIG_PATH));
        if (object != null) {
            configData = object.toJavaObject(ConfigData.class);
        }
        if (configData == null) {
            configData = new ConfigData();
        }
        configData.apply();
        CommandManager.INSTANCE.registerCommand(CommandLine0.INSTANCE, true);
        GlobalEventChannel.INSTANCE.registerListenerHost(new MyListenerHost());
        receiver = new PermissionId("io.github.kloping.DetectRecallPlugin", "receiver");
        monitor = new PermissionId("io.github.kloping.DetectRecallPlugin", "monitor");
        try {
            receiverPerm = PermissionService.getInstance().register(receiver, "接收者", INSTANCE.getParentPermission());
            monitorPerm = PermissionService.getInstance().register(monitor, "监听", INSTANCE.getParentPermission());
        } catch (PermissionRegistryConflictException e) {
            e.printStackTrace();
        }
    }
}
