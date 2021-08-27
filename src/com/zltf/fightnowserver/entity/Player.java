package com.zltf.fightnowserver.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.zltf.fightnowserver.utils.InfoManager;

import java.util.Timer;
import java.util.TimerTask;

public class Player {

    private static int newId = 0;

    public Player() {
        id = newId++;
        healthPoint = InfoManager.PLAYER_HP;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(getHealthPoint() <= 0) {
                    cancel();
                    return;
                }
                float hpRestore = InfoManager.PLAYER_HP_RESTORE/InfoManager.PKG_NUM;
                if(getHealthPoint() < InfoManager.PLAYER_HP-hpRestore) {
                    setHealthPoint(getHealthPoint()+hpRestore);
                } else {
                    setHealthPoint(InfoManager.PLAYER_HP);
                }
            }
        },0, 1000/InfoManager.PKG_NUM);
    }

    @JSONField(name = "player_id")
    int id;

    @JSONField(name = "position_x")
    float x;

    @JSONField(name = "position_y")
    float y;

    @JSONField(name = "rotation")
    float rotation;

    @JSONField(name = "health_point")
    float healthPoint;

    @JSONField(name = "time_stamp")
    long timeStamp;

    @JSONField(serialize = false)
    private String host;

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRotation() {
        return rotation;
    }

    public float getHealthPoint() {
        return healthPoint;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setHealthPoint(float healthPoint) {
        this.healthPoint = healthPoint;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void move(float paraX, float paraY) {
        float move = InfoManager.PLAYER_SPEED/InfoManager.PKG_NUM;
        setX(getX() + move*paraX);
        setY(getY() + move*paraY);
        if(getX()<0) {
            setX(0);
        }
        if(getX()+InfoManager.PLAYER_SIZE>InfoManager.WORlD_WIDTH) {
            setX(InfoManager.WORlD_WIDTH - InfoManager.PLAYER_SIZE);
        }
        if(getY()<0) {
            setY(0);
        }
        if(getY()+InfoManager.PLAYER_SIZE>InfoManager.WORLD_HEIGHT) {
            setY(InfoManager.WORLD_HEIGHT - InfoManager.PLAYER_SIZE);
        }

        setRotation((float)Math.toDegrees((paraX>0)?Math.atan(paraY/paraX):Math.PI+Math.atan(paraY/paraX)));
    }

    public void attacked() {
        setHealthPoint(getHealthPoint() - InfoManager.BULLET_ATK);
        if(getHealthPoint() <= 0) {
            setHealthPoint(0);
        }
    }
}
