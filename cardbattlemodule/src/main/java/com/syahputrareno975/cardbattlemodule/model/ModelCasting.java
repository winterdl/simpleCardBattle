package com.syahputrareno975.cardbattlemodule.model;

import cardBattle.CardBattle;
import com.syahputrareno975.cardbattlemodule.model.card.AllCardModel;
import com.syahputrareno975.cardbattlemodule.model.card.CardModel;
import com.syahputrareno975.cardbattlemodule.model.player.AllPlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.AllPlayerWithCardsModel;
import com.syahputrareno975.cardbattlemodule.model.player.PlayerModel;
import com.syahputrareno975.cardbattlemodule.model.playerBattleResult.AllPlayerBattleResultModel;
import com.syahputrareno975.cardbattlemodule.model.playerBattleResult.PlayerBattleResultModel;
import com.syahputrareno975.cardbattlemodule.model.playerWithCard.PlayerWithCardsModel;
import com.syahputrareno975.cardbattlemodule.model.room.AllRoomModel;
import com.syahputrareno975.cardbattlemodule.model.room.RoomDataModel;
import com.syahputrareno975.cardbattlemodule.model.roomResult.EndResultModel;
import com.syahputrareno975.cardbattlemodule.model.roomReward.RoomRewardModel;

import java.util.ArrayList;

public class ModelCasting {

        // player
        public static PlayerModel toPlayerModel( CardBattle.player p)  {
            return new PlayerModel(p.getId(), p.getName(), p.getAvatar(), p.getLevel(), p.getCash(),p.getExp(),p.getMaxExp(),p.getMaxReserveSlot(),p.getMaxDeckSlot());
        }
        public static CardBattle.player toPlayerModelGRPC( PlayerModel p)  {
            return CardBattle.player.newBuilder()
                .setId(p.Id)
                .setName(p.Name)
                .setAvatar(p.Avatar)
                .setLevel(p.Level)
                .setCash(p.Cash)
                .setExp(p.Exp)
                .setMaxExp(p.MaxExp)
                .setMaxReserveSlot(p.MaxReserveSlot)
                .setMaxDeckSlot(p.MaxDeckSlot)
                .build();
        }
        public static AllPlayerModel toAllPlayerModel(CardBattle.allPlayer p){
            ArrayList<PlayerModel> player = new ArrayList<PlayerModel>();
            for (CardBattle.player i : p.getPlayersList()){
                player.add(toPlayerModel(i));
            }
            return new AllPlayerModel(player);
        }
        public static CardBattle.allPlayer toAllPlayerModelGRPC(AllPlayerModel p) {
            ArrayList<CardBattle.player> player = new ArrayList<CardBattle.player>();
            for (PlayerModel i : p.Players){
                player.add(toPlayerModelGRPC(i));
            }
            return CardBattle.allPlayer.newBuilder()
                .addAllPlayers(player)
                .build();
        }

        // card
        public static CardModel toCardModel(CardBattle.card p)  {
            return new CardModel(p.getId(),p.getImage(),p.getName(),p.getLevel(), p.getAtk(),p.getDef(),p.getPrice(),p.getColor());
        }
        public static CardBattle.card  toCardModelGRPC(CardModel p){
            return CardBattle.card.newBuilder()
                .setId(p.Id)
                .setImage(p.Image)
                .setLevel(p.Level)
                .setAtk(p.Atk)
                .setDef(p.Def)
                .setName(p.Name)
                .setPrice(p.Price)
                .setColor(p.Color)
                .build();
        }
        public static  AllCardModel toAllCardModel(CardBattle.allCard p) {
            ArrayList<CardModel> cards = new ArrayList<CardModel>();
            for (CardBattle.card i : p.getCardsList()){
                cards.add(toCardModel(i));
            }
            return new AllCardModel(cards);
        }
        public static CardBattle.allCard toAllCardModelGRPC( AllCardModel p) {
            ArrayList<CardBattle.card> cards = new ArrayList<CardBattle.card>();
            for (CardModel i : p.Cards){
                cards.add(toCardModelGRPC(i));
            }
            return CardBattle.allCard.newBuilder()
                .addAllCards(cards)
                .build();
        }

        // reward
        public static RoomRewardModel toRoomRewardModel(CardBattle.roomReward p) {
            return new RoomRewardModel(toAllCardModel(
                CardBattle.allCard.newBuilder()
                    .addAllCards(p.getCardRewardList())
                    .build()
            ).Cards, p.getCashReward(), p.getExpReward());
        }

        public static CardBattle.roomReward toRoomRewardModelGRPC(RoomRewardModel p)  {
            return CardBattle.roomReward.newBuilder()
                .addAllCardReward(toAllCardModelGRPC(new AllCardModel(p.CardsReward)).getCardsList())
                .setCashReward(p.CashReward)
                .setExpReward(p.ExpReward)
                .build();
        }


        // room
        public static RoomDataModel toRoomModel(CardBattle.roomData p)  {
            return new RoomDataModel(
               p.getId(),
               p.getRoomName(),
               toAllPlayerWithCardsModel(
                   CardBattle.allPlayerWithCards.newBuilder()
                       .addAllPlayers(p.getPlayersList())
                       .build()
               ).Players,
                p.getMaxPlayer(),
                p.getMaxPlayerDeck(),
                p.getMaxCurrentDeployment(),
                p.getMaxDeployment(),
                p.getEachPlayerHealth(),
                p.getCoolDownTime(),
                toRoomRewardModel(p.getReward())
            );
        }
        public static CardBattle.roomData toRoomModelGRPC(RoomDataModel p)  {
            return CardBattle.roomData.newBuilder()
                .setId(p.Id)
                .setRoomName(p.RoomName)
                .addAllPlayers(
                    toAllPlayerWithCardsModelGRPC(
                        new AllPlayerWithCardsModel(
                            p.Players
                        )
                    ).getPlayersList()
                )
                .setMaxPlayer(p.MaxPlayer)
                .setMaxPlayerDeck(p.MaxPlayerDeck)
                .setMaxCurrentDeployment(p.MaxCurrentDeployment)
                .setMaxDeployment(p.MaxDeploment)
                .setEachPlayerHealth(p.EachPlayerHealth)
                .setCoolDownTime(p.CoolDownTime)
                .setReward(toRoomRewardModelGRPC(p.Reward))
                .build();
        }
        public static AllRoomModel toAllRoomModel(CardBattle.allRoom p)  {
        ArrayList<RoomDataModel> cards = new ArrayList<RoomDataModel>();
            for (CardBattle.roomData i : p.getRoomsList()){
                cards.add(toRoomModel(i));
            }
            return new AllRoomModel(cards);
        }
        public static CardBattle.allRoom toAllRoomModelGRPC(AllRoomModel p) {
        ArrayList<CardBattle.roomData> rooms = new ArrayList<CardBattle.roomData>();
            for (RoomDataModel i: p.Rooms){
                rooms.add(toRoomModelGRPC(i));
            }
            return CardBattle.allRoom.newBuilder()
                .addAllRooms(rooms)
                .build();
        }


        // player with deck
        public static PlayerWithCardsModel toPlayerWithCardsModel(CardBattle.playerWithCards p)  {
            return new PlayerWithCardsModel(
                toPlayerModel(p.getOwner()),
                toAllCardModel(
                    CardBattle.allCard.newBuilder()
                        .addAllCards(p.getDeckList())
                        .build()
                ).Cards,
                toAllCardModel(
                    CardBattle.allCard.newBuilder()
                        .addAllCards(p.getReserveList())
                        .build()
                ).Cards,
                toAllCardModel(
                    CardBattle.allCard.newBuilder()
                        .addAllCards(p.getDeployedList())
                        .build()
                ).Cards,
                p.getHp()
            );
        }

        public static CardBattle.playerWithCards toPlayerWithCardsModelGRPC(PlayerWithCardsModel p)  {
            return CardBattle.playerWithCards.newBuilder()
                .setOwner(toPlayerModelGRPC(p.Owner))
                .addAllDeck(toAllCardModelGRPC(new AllCardModel(p.Deck)).getCardsList())
                .addAllDeployed(toAllCardModelGRPC(new AllCardModel(p.Deployed)).getCardsList())
                .addAllReserve(toAllCardModelGRPC(new AllCardModel(p.Reserve)).getCardsList())
                .setHp(p.Hp)
                .build();
        }

        public static AllPlayerWithCardsModel toAllPlayerWithCardsModel(CardBattle.allPlayerWithCards p) {
            ArrayList<PlayerWithCardsModel> players = new ArrayList<PlayerWithCardsModel>();
            for (CardBattle.playerWithCards i : p.getPlayersList()){
                players.add(toPlayerWithCardsModel(i));
            }
            return new AllPlayerWithCardsModel(
                players
            );
        }

        public static CardBattle.allPlayerWithCards toAllPlayerWithCardsModelGRPC(AllPlayerWithCardsModel p) {
            ArrayList<CardBattle.playerWithCards> players = new ArrayList<CardBattle.playerWithCards>();
            for (PlayerWithCardsModel i : p.Players){
                players.add(toPlayerWithCardsModelGRPC(i));
            }
            return CardBattle.allPlayerWithCards.newBuilder()
                .addAllPlayers(players)
                .build();
        }

        public static PlayerBattleResultModel toPlayerBattleResultModelModel(CardBattle.playerBattleResult p) {
            return new PlayerBattleResultModel(toPlayerModel(p.getOwner()),p.getDamageReceive(),p.getEnemyAtk(),p.getOwnerDef());
        }

        public static  CardBattle.playerBattleResult toPlayerBattleResultModelModelGRPC(PlayerBattleResultModel p){
           return CardBattle.playerBattleResult.newBuilder()
               .setOwner(toPlayerModelGRPC(p.Owner))
               .setDamageReceive(p.DamageReceive)
               .setEnemyAtk(p.EnemyAtk)
               .setOwnerDef(p.OwnerDef)
               .build();
        }


        public static AllPlayerBattleResultModel toAllPlayerBattleResultModelModel(CardBattle.allPlayerBattleResult p) {
            ArrayList<PlayerBattleResultModel> results = new ArrayList<PlayerBattleResultModel>();
            for (CardBattle.playerBattleResult i : p.getResultsList()){
                results.add(toPlayerBattleResultModelModel(i));
            }
            return new AllPlayerBattleResultModel(results);
        }
        public static CardBattle.allPlayerBattleResult toAllPlayerBattleResultModelModelGRPC(AllPlayerBattleResultModel p) {
            ArrayList<CardBattle.playerBattleResult> results = new ArrayList<CardBattle.playerBattleResult>();
            for (PlayerBattleResultModel i : p.Results){
                results.add(toPlayerBattleResultModelModelGRPC(i));
            }
            return CardBattle.allPlayerBattleResult.newBuilder()
                .addAllResults(results)
                .build();
        }

        public static EndResultModel toEndResultModel(CardBattle.endResult p) {
            return new EndResultModel(
                toPlayerModel(p.getWinner()),
                toAllPlayerBattleResultModelModel(
                    CardBattle.allPlayerBattleResult.newBuilder()
                        .addAllResults(p.getAllBattleResultList()).build()).Results,
                p.getFlagResult(),
                toRoomRewardModel(p.getReward()));
        }
        public static CardBattle.endResult toEndResultModelGRPC(EndResultModel p) {
            return CardBattle.endResult.newBuilder()
                .setWinner(toPlayerModelGRPC(p.Winner))
                .addAllAllBattleResult(
                    toAllPlayerBattleResultModelModelGRPC(new AllPlayerBattleResultModel(p.allBattleResult)).getResultsList())
                .setReward(toRoomRewardModelGRPC(p.Reward))
                .setFlagResult(p.FlagResult)
                .build();
        }
}