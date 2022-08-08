package io.github.kloping;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.permission.AbstractPermitteeId;
import net.mamoe.mirai.console.permission.PermissionService;
import net.mamoe.mirai.console.permission.PermitteeId;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageRecallEvent;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author github.kloping
 */
public class ListenHost extends SimpleListenerHost implements Runnable {
    private Map<Integer, Message> msMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ListenHost() {
        scheduler.scheduleWithFixedDelay(this, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        int max = ConfigData.INSTANCE.getMil() * 60;
        Iterator<Integer> iterator = msMap.keySet().iterator();
        while (iterator.hasNext()) {
            int time = iterator.next();
            time += max;
            if (time < System.currentTimeMillis() / 1000)
                iterator.remove();
        }
    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    @EventHandler
    public void onMessage(GroupMessageEvent event) {
        msMap.put(event.getTime(), event.getMessage());
        Group group = event.getGroup();
        if (PermissionService.hasPermission(new AbstractPermitteeId.ExactGroup(group.getId()), DetectRecallPlugin.INSTANCE.monitorPerm)) {
            FlashImage flashImage = getFlashImage(event.getMessage());
            if (flashImage != null) {
                MessageChainBuilder builder = new MessageChainBuilder();
                builder.append("'").append(event.getSenderName()).append("(" + event.getSender().getId() + ")'在群聊'").append(event.getGroup().getName())
                        .append("(" + group.getId() + ")'发送闪照:").append(flashImage.getImage());
                Message message = builder.build();
                for (Contact contact : all(event.getBot())) {
                    contact.sendMessage(message);
                }
            }
        }
    }

    @EventHandler
    public void onMessage(FriendMessageEvent event) {
        msMap.put(event.getTime(), event.getMessage());
        Friend friend = event.getFriend();
        if (PermissionService.hasPermission(new AbstractPermitteeId.ExactFriend(friend.getId()), DetectRecallPlugin.INSTANCE.monitorPerm)) {
            FlashImage flashImage = getFlashImage(event.getMessage());
            if (flashImage != null) {
                MessageChainBuilder builder = new MessageChainBuilder();
                builder.append("'").append(event.getSenderName()).append("(" + friend.getId() + ")'在私聊").append("发送闪照:").append(flashImage.getImage());
                Message message = builder.build();
                for (Contact contact : all(event.getBot())) {
                    contact.sendMessage(message);
                }
            }
        }
    }

    public FlashImage getFlashImage(MessageChain chain) {
        for (SingleMessage singleMessage : chain) {
            if (singleMessage instanceof FlashImage) {
                return (FlashImage) singleMessage;
            }
        }
        return null;
    }

    @EventHandler
    public void onMessage(MessageRecallEvent.GroupRecall event) {
        int time = event.getMessageTime();
        if (msMap.containsKey(time)) {
            Message m0 = msMap.get(time);
            Member member = event.getOperator();
            Group group = event.getGroup();
            if (PermissionService.hasPermission(new AbstractPermitteeId.ExactGroup(group.getId()), DetectRecallPlugin.INSTANCE.monitorPerm)) {
                MessageChainBuilder builder = new MessageChainBuilder();
                builder.append("'").append(member.getNameCard()).append("(" + member.getId() + ")").append("'在群聊'").append(event.getGroup().getName())
                        .append("(" + group.getId() + ")'撤回消息:").append(m0);
                Message message = builder.build();
                for (Contact contact : all(event.getBot())) {
                    contact.sendMessage(message);
                }
            }
        }
    }

    @EventHandler
    public void onMessage(MessageRecallEvent.FriendRecall event) {
        int time = event.getMessageTime();
        if (msMap.containsKey(time)) {
            Message m0 = msMap.get(time);
            Friend friend = event.getAuthor();
            if (PermissionService.hasPermission(new AbstractPermitteeId.ExactFriend(friend.getId()), DetectRecallPlugin.INSTANCE.monitorPerm)) {
                MessageChainBuilder builder = new MessageChainBuilder();
                builder.append("'").append(friend.getNick()).append("(" + friend.getId() + ")'在私聊").append("撤回了:").append(m0);
                Message message = builder.build();
                for (Contact contact : all(event.getBot())) {
                    contact.sendMessage(message);
                }
            }
        }
    }

    public Set<Contact> all(Bot bot) {
        Set<Contact> users = new HashSet<>();
        for (Friend friend : bot.getFriends()) {
            if (friend.getId() == bot.getId()) continue;
            PermitteeId permitteeId = new AbstractPermitteeId.ExactFriend(friend.getId());
            if (PermissionService.hasPermission(permitteeId, DetectRecallPlugin.INSTANCE.receiverPerm)) {
                users.add(friend);
            }
        }
        for (Group group : bot.getGroups()) {
            PermitteeId permitteeId = new AbstractPermitteeId.ExactGroup(group.getId());
            if (PermissionService.hasPermission(permitteeId, DetectRecallPlugin.INSTANCE.receiverPerm)) {
                users.add(group);
            }
        }
        users.add(bot.getAsFriend());
        return users;
    }
}
