package com.zltf.fightnowserver.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class Bullet {
    public void setId(int id) {
        this.id = id;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDirection() {
        return direction;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    @JSONField(name = "bullet_id")
    int id;

    @JSONField(name = "owner_id")
    int ownerId;

    @JSONField(name = "position_x")
    float x;

    @JSONField(name = "position_y")
    float y;

    @JSONField(name = "direction")
    float direction;

    @JSONField(serialize = false)
    float startX;

    @JSONField(serialize = false)
    float startY;
}
