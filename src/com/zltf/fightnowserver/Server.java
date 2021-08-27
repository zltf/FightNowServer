package com.zltf.fightnowserver;

import com.alibaba.fastjson.JSON;
import com.zltf.fightnowserver.action.ActionAttack;
import com.zltf.fightnowserver.action.ActionMove;
import com.zltf.fightnowserver.entity.Bullet;
import com.zltf.fightnowserver.entity.Player;
import com.zltf.fightnowserver.entity.Room;
import com.zltf.fightnowserver.utils.InfoManager;
import com.zltf.fightnowserver.utils.UDPManager;

import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
    Room room;
    UDPManager udpManager;

    public Server() {
        // CopyOnWriteArrayList<Room> rooms = new CopyOnWriteArrayList<>();

        udpManager = new UDPManager();

        room = new Room(udpManager);

        // 消息接收线程
        new Thread(() -> {
            while(true) {
                DatagramPacket datagramPacket = udpManager.recv();
                String recv = UDPManager.getDataFromRecv(datagramPacket);
                char op = recv.charAt(0);
                String data = recv.substring(1);
                handleRecv(op, data, datagramPacket.getAddress().getHostAddress());
            }
        }).start();

        new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
			    for(Player toPlayer: room.getPlayers()) {
                    for(Player player: room.getPlayers()) {
                        player.setTimeStamp(System.currentTimeMillis());
                        String str = JSON.toJSONString(player);
                        udpManager.send(toPlayer.getHost(), "p" + str);
                    }

                    for(Bullet bullet: room.getBullets()) {
                        String str = JSON.toJSONString(bullet);
                        udpManager.send(toPlayer.getHost(), "b" + str);
                    }
                }
			}
		},0, 1000/ InfoManager.PKG_NUM);
    }

    public void handleRecv(char op, String data, String host) {
        switch (op) {
            case 'm':
                // 移动玩家
                ActionMove actionMove = JSON.parseObject(data, ActionMove.class);
                room.getPlayerById(actionMove.getPlayerId()).move(actionMove.getDirPadParaX(), actionMove.getDirPadParaY());
                break;
            case 'a':
                // 攻击
                ActionAttack actionAttack = JSON.parseObject(data, ActionAttack.class);
                room.attack(actionAttack.getPlayerId(), actionAttack.getBulletId(), actionAttack.getDirection());
                for(Player toPlayer: room.getPlayers()) {
                    udpManager.send(toPlayer.getHost(), "a" + data);
                }
                break;
            case 'j':
                // 加入新玩家
                Player player = room.addPlayer(host);
                if (player != null) {
                    udpManager.send(host, "j" + JSON.toJSONString(player));
                } else {
                    udpManager.send(host, "e");
                }
                break;
        }
    }
}
