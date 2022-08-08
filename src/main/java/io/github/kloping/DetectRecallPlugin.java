package io.github.kloping;

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

/**
 * @author github.kloping
 */
public class DetectRecallPlugin extends JavaPlugin {
    public static final DetectRecallPlugin INSTANCE = new DetectRecallPlugin();

    public DetectRecallPlugin() {
        super(new JvmPluginDescriptionBuilder("io.github.kloping.DetectRecallPlugin", "1.0").info("监控撤回闪照").build());
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
        this.reloadPluginConfig(ConfigData.INSTANCE);
        CommandManager.INSTANCE.registerCommand(CommandLine0.INSTANCE, true);
        GlobalEventChannel.INSTANCE.registerListenerHost(new ListenHost());
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