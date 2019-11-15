package com.syahputrareno975.cardbattlemodule.model.player;

import java.io.Serializable;

public class PlayerModel implements Serializable {
    public String Id = "";
    public String  Name = "";
    public String Avatar = "";
    public int Level = 0;
    public Long Cash = 0L;
    public Long Exp  = 0L;
    public Long MaxExp = 0L;
    public int MaxReserveSlot = 0;
    public int MaxDeckSlot = 0;

    public PlayerModel() {
    }

    public PlayerModel(String id, String name, String avatar, int level, Long cash, Long exp, Long maxExp, int maxReserveSlot, int maxDeckSlot) {
        Id = id;
        Name = name;
        Avatar = avatar;
        Level = level;
        Cash = cash;
        Exp = exp;
        MaxExp = maxExp;
        MaxReserveSlot = maxReserveSlot;
        MaxDeckSlot = maxDeckSlot;
    }
}