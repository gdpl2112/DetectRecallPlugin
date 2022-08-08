撤回/闪照监听插件


- 监听所有群
  
  
    /perm permit g* io.github.kloping.DetectRecallPlugin:monitor

- 监听所有h好友
  
  
    /perm permit f* io.github.kloping.DetectRecallPlugin:monitor

- 添加接收者


    /perm permit u[QQ] io.github.kloping.DetectRecallPlugin:receiver


- 命令


    /detectRecall setMil <分钟>    # 设置撤回消息监听最大延时时间