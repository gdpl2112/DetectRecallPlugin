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
import net.mamoe.mirai.event.events.MessageEvent;
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
public class MyListenerHost extends SimpleListenerHost implements Runnable {
    private List<MessageEvent> events = new ArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public MyListenerHost() {
        scheduler.scheduleWithFixedDelay(this, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        int max = DetectRecallPlugin.INSTANCE.configData.getMil() * 60;
        Iterator<MessageEvent> iterator = events.iterator();
        while (iterator.hasNext()) {
            MessageEvent event = iterator.next();
            int time = event.getTime();
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
        events.add(event);
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
        events.add(event);
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

    @EventHandler
    public void onMessage(MessageRecallEvent.GroupRecall event) {
        Group group = event.getGroup();
        Member member = event.getOperator();
        if (PermissionService.hasPermission(new AbstractPermitteeId.ExactGroup(group.getId()), DetectRecallPlugin.INSTANCE.monitorPerm)) {
//            if (ConfigData.INSTANCE.containsBlacklist(group.getId())) return;
            Message m0 = getMessage(event);
            if (m0 != null) {
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
        Friend friend = event.getAuthor();
        if (PermissionService.hasPermission(new AbstractPermitteeId.ExactFriend(friend.getId()), DetectRecallPlugin.INSTANCE.monitorPerm)) {
//            if (ConfigData.INSTANCE.containsBlacklist(friend.getId())) return;
            Message m0 = getMessage(event);
            if (m0 != null) {
                MessageChainBuilder builder = new MessageChainBuilder();
                builder.append("'").append(friend.getNick()).append("(" + friend.getId() + ")'在私聊").append("撤回了:").append(m0);
                Message message = builder.build();
                for (Contact contact : all(event.getBot())) {
                    contact.sendMessage(message);
                }
            }
        }
    }

    public synchronized Message getMessage(MessageRecallEvent event) {
        for (MessageEvent e1 : events) {
            MessageSource source = e1.getSource();
            if (event.getMessageTime() == e1.getTime()) {
                if (event.getMessageIds()[0] == source.getIds()[0]) {
                    if (event.getMessageInternalIds()[0] == source.getInternalIds()[0])
                        return e1.getMessage();
                }
            }
        }
        return null;
    }

    public FlashImage getFlashImage(MessageChain chain) {
        for (SingleMessage singleMessage : chain) {
            if (singleMessage instanceof FlashImage) {
                return (FlashImage) singleMessage;
            }
        }
        return null;
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
