package com.zltf.fightnowserver.action;

import com.alibaba.fastjson.annotation.JSONField;

public class ActionAttack {
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setBulletId(int bulletId) {
        this.bulletId = bulletId;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getBulletId() {
        return bulletId;
    }

    public float getDirection() {
        return direction;
    }

    @JSONField(name = "room_id")
    int roomId;

    @JSONField(name = "player_id")
    int playerId;

    @JSONField(name = "bullet_id")
    int bulletId;

    @JSONField(name = "direction")
    float direction;
}
