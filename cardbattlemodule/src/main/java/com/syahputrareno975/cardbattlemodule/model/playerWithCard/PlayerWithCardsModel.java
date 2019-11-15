package com.syahputrareno975.cardbattlemodule.model.playerWithCard;

import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayerWithCardsModel implements Serializable {
    public PlayerModel Owner = new PlayerModel();
    public ArrayList<CardModel> Deck = new ArrayList<CardModel>();
    public ArrayList<CardModel> Reserve = new ArrayList<CardModel>();
    public ArrayList<CardModel> Deployed = new ArrayList<CardModel>();
    public Long Hp = 0L;

    public PlayerWithCardsModel() {
    }

    public PlayerWithCardsModel(PlayerModel owner, ArrayList<CardModel> deck, ArrayList<CardModel> reserve, ArrayList<CardModel> deployed, Long hp) {
        Owner = owner;
        Deck = deck;
        Reserve = reserve;
        Deployed = deployed;
        Hp = hp;
    }

    public void copyFromObject(PlayerWithCardsModel p) {

        this.Reserve.clear();
        this.Deck.clear();
        this.Deployed.clear();

        this.Owner = p.Owner;
        this.Deck.addAll(p.Deck);
        this.Reserve.addAll(p.Reserve);
        this.Deployed.addAll(p.Deployed);

        this.Hp = p.Hp;
    }


}