package com.syahputrareno975.cardbattlemodule.model.card;

import java.io.Serializable;

public class CardModel implements Serializable {
    public String Id = "";
    public String Image = "";
    public String Name = "";
    public int Level  = 0;
    public Long Atk = 0L;
    public Long Def = 0L;
    public Long Price= 0L;
    public int Color = 0;

    public int Flag = 0;
    public String Message = "";


    public CardModel() {
    }

    public CardModel(String id, String image, String name, int level, Long atk, Long def, Long price, int color) {
        Id = id;
        Image = image;
        Name = name;
        Level = level;
        Atk = atk;
        Def = def;
        Price = price;
        Color = color;
    }
}