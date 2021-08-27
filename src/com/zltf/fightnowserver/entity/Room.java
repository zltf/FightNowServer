package com.zltf.fightnowserver.entity;

import com.alibaba.fastjson.JSON;
import com.zltf.fightnowserver.utils.InfoManager;
import com.zltf.fightnowserver.utils.UDPManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {
    int id;

    CopyOnWriteArrayList<Player> players;
    CopyOnWriteArrayList<Bullet> bullets;

    private static int newId = 0;

    UDPManager udpManager;

    public Room(UDPManager udpManager) {
        this.udpManager = udpManager;

        players = new CopyOnWriteArrayList<>();
        bullets = new CopyOnWriteArrayList<>();
        id = newId++;
    }

    public Player addPlayer(String host) {
        if(!isFull()) {
            Player player = new Player();
            player.setHost(host);
            switch(players.size()) {
                case 0:
                    player.setPosition(InfoManager.WORlD_WIDTH/5, InfoManager.WORLD_HEIGHT/3);
                    break;
                case 1:
                    player.setPosition(InfoManager.WORlD_WIDTH/5*4, InfoManager.WORLD_HEIGHT/3*2);
                    break;
            }
            players.add(player);
            return player;
        }
        return null;
    }

    public Player getPlayerById(int id) {
        for(Player player: players) {
            if(player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    public boolean isFull() {
        return players.size() == 2;
    }

    public int getId() {
        return id;
    }

    public void attack(int playerId,int bulletId, float direction) {
        Player player = getPlayerById(playerId);
        Bullet bullet = new Bullet();
        bullet.setId(bulletId);
        bullet.setOwnerId(playerId);
        bullet.setX(player.getX()+InfoManager.PLAYER_SIZE/2);
        bullet.setY(player.getY()+InfoManager.PLAYER_SIZE/2-InfoManager.ATTACK_RANGE/2);
        bullet.setStartX(bullet.getX());
        bullet.setStartY(bullet.getY());
        bullet.setDirection(direction);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // 子弹距离检测
                float x = bullet.getX()-bullet.getStartX();
                float y = bullet.getY()-bullet.getStartY();
                float distance = (float)Math.sqrt(x*x + y*y);
                if (distance > InfoManager.ATTACK_DISTANCE-InfoManager.BULLET_LENGTH) {
                    removeBullet(bullet);
                    cancel();
                }

                // 子弹移动
                float move = InfoManager.ATTACK_SPEED/InfoManager.PKG_NUM;
                bullet.setX(bullet.getX() + move*(float)Math.cos(Math.toRadians(bullet.getDirection())));
                bullet.setY(bullet.getY() + move*(float)Math.sin(Math.toRadians(bullet.getDirection())));

                // 碰撞检测
                for(int i = bullets.size()-1; i>=0; --i) {
                    // 预计算矩形3点的坐标， 加快运算速度
                    float x0, y0, x1, y1, x2, y2;
                    x1 = bullets.get(i).getX();
                    y1 = bullets.get(i).getY()+InfoManager.ATTACK_RANGE/2;
                    x0 = x1 + InfoManager.BULLET_LENGTH*(float)Math.cos(Math.toRadians(bullets.get(i).getDirection()))/2;
                    y0 = y1 + InfoManager.BULLET_LENGTH*(float)Math.sin(Math.toRadians(bullets.get(i).getDirection()))/2;
                    x2 = x0 + InfoManager.ATTACK_RANGE*(float)Math.sin(Math.toRadians(bullets.get(i).getDirection()))/2;
                    y2 = y0 - InfoManager.ATTACK_RANGE*(float)Math.cos(Math.toRadians(bullets.get(i).getDirection()))/2;

                    boolean collisionFlag = false;
                    for(Player player:players) {
                        if(player.getId() != bullets.get(i).getOwnerId() && collisionDetection(player, x0, y0, x1, y1, x2, y2)) {
                            // 玩家被攻击，扣血
                            player.attacked();
                            // 判断玩家是否死亡
                            if(player.getHealthPoint() <= 0) {
                                // 广播玩家死亡
                                for(Player toPlayer: players) {
                                    udpManager.send(toPlayer.getHost(), "d" + JSON.toJSONString(player));
                                }
                            }
                            collisionFlag = true;
                        }
                    }
                    // 移除子弹
                    if(collisionFlag) {
                        removeBullet(bullets.get(i));
                        cancel();
                    }
                }
            }
        },0, 1000/InfoManager.PKG_NUM);
        bullets.add(bullet);
    }

    public CopyOnWriteArrayList<Player> getPlayers() {
        return players;
    }

    public CopyOnWriteArrayList<Bullet> getBullets() {
        return bullets;
    }

    public boolean collisionDetection(Player player, float x0, float y0, float x1, float y1, float x2, float y2) {
        // 圆形参数
        float x = player.getX()+InfoManager.PLAYER_SIZE/2;
        float y = player.getY()+InfoManager.PLAYER_SIZE/2;
        float r = InfoManager.PLAYER_SIZE/2;

        float w1 = DistanceFromPointToPoint(x0, y0, x2, y2);
        float h1 = DistanceFromPointToPoint(x0, y0, x1, y1);
        float w2 = DistanceFromPointToLine(x, y, x0, y0, x1, y1);
        float h2 = DistanceFromPointToLine(x, y, x0, y0, x2, y2);

        if (w2 > w1 + r)
            return false;
        if (h2 > h1 + r)
            return false;

        if (w2 <= w1)
            return true;
        if (h2 <= h1)
            return true;
        return (w2 - w1) * (w2 - w1) + (h2 - h1) * (h2 - h1) <= r * r;
    }

    // 计算两点之间的距离
    float DistanceFromPointToPoint(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    // 计算点(x, y)到经过两点(x1, y1)和(x2, y2)的直线的距离
    float DistanceFromPointToLine(float x, float y, float x1, float y1, float x2, float y2) {
        float a = y2 - y1;
        float b = x1 - x2;
        float c = x2 * y1 - x1 * y2;
        // 分母不能为0
        if(a == 0 && b == 0) {
            a = 0.000001f;
        }
        return (float)(Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b * b));
    }

    void removeBullet(Bullet bullet) {
        // 广播子弹移除
        for(Player toPlayer: players) {
            udpManager.send(toPlayer.getHost(), "r" + JSON.toJSONString(bullet));
        }
        bullets.remove(bullet);
    }
}
