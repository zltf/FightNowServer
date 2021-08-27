package com.zltf.fightnowserver.action;

import com.alibaba.fastjson.annotation.JSONField;

public class ActionMove {
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setDirPadParaX(float dirPadParaX) {
        this.dirPadParaX = dirPadParaX;
    }

    public void setDirPadParaY(float dirPadParaY) {
        this.dirPadParaY = dirPadParaY;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public float getDirPadParaX() {
        return dirPadParaX;
    }

    public float getDirPadParaY() {
        return dirPadParaY;
    }

    @JSONField(name = "room_id")
    int roomId;

    @JSONField(name = "player_id")
    int playerId;

    @JSONField(name = "dir_pad_para_x")
    float dirPadParaX;

    @JSONField(name = "dir_pad_para_y")
    float dirPadParaY;
}
