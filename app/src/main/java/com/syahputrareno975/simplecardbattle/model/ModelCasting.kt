package com.syahputrareno975.simplecardbattle.model

import cardBattle.CardBattle
import com.syahputrareno975.simplecardbattle.model.card.AllCardModel
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.AllPlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.AllPlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.AllRoomModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel

class ModelCasting {
    companion object {

        // player
        fun toPlayerModel(p : CardBattle.player) : PlayerModel {
            return PlayerModel(p.id, p.name, p.avatar, p.level, p.cash)
        }
        fun toPlayerModelGRPC(p : PlayerModel) : CardBattle.player {
            return CardBattle.player.newBuilder()
                .setId(p.Id)
                .setName(p.Name)
                .setAvatar(p.Avatar)
                .setLevel(p.Level)
                .setCash(p.Cash)
                .build()
        }
        fun toAllPlayerModel(p : CardBattle.allPlayer) : AllPlayerModel {
            val player = ArrayList<PlayerModel>()
            for (i in p.playersList){
                player.add(toPlayerModel(i))
            }
            return AllPlayerModel(player)
        }
        fun toAllPlayerModelGRPC(p : AllPlayerModel) : CardBattle.allPlayer{
            val player = ArrayList<CardBattle.player>()
            for (i in p.Players){
                player.add(toPlayerModelGRPC(i))
            }
            return CardBattle.allPlayer.newBuilder()
                .addAllPlayers(player)
                .build()
        }

        // card
        fun toCardModel(p : CardBattle.card) : CardModel {
            return CardModel(p.id,p.image,p.name,p.level, p.atk,p.def,p.price,p.color)
        }
        fun toCardModelGRPC(p : CardModel) : CardBattle.card {
            return CardBattle.card.newBuilder()
                .setId(p.Id)
                .setImage(p.Image)
                .setLevel(p.Level)
                .setAtk(p.Atk)
                .setDef(p.Def)
                .setName(p.Name)
                .setPrice(p.Price)
                .setColor(p.Color)
                .build()
        }
        fun toAllCardModel(p : CardBattle.allCard) : AllCardModel {
            val cards = ArrayList<CardModel>()
            for (i in p.cardsList){
                cards.add(toCardModel(i))
            }
            return  AllCardModel(cards)
        }
        fun toAllCardModelGRPC(p : AllCardModel) : CardBattle.allCard{
            val cards = ArrayList<CardBattle.card>()
            for (i in p.Cards){
                cards.add(toCardModelGRPC(i))
            }
            return CardBattle.allCard.newBuilder()
                .addAllCards(cards)
                .build()
        }


        // room
        fun toRoomModel(p : CardBattle.roomData) : RoomDataModel {
            return RoomDataModel(
               p.id,
               p.roomName,
               toAllPlayerWithCardsModel(
                   CardBattle.allPlayerWithCards.newBuilder()
                       .addAllPlayers(p.playersList.toList())
                       .build()
               ).Players,
                p.maxPlayer,
                p.maxPlayerDeck,
                p.maxDeploment,
                p.eachPlayerHealth,
                p.coolDownTime,
                toAllCardModel(
                    CardBattle.allCard.newBuilder()
                        .addAllCards(p.cardRewardList)
                        .build()
                ).Cards,
                p.cashReward,
                p.levelReward
            )
        }
        fun toRoomModelGRPC(p : RoomDataModel) : CardBattle.roomData {
            return CardBattle.roomData.newBuilder()
                .setId(p.Id)
                .setRoomName(p.RoomName)
                .addAllPlayers(
                    toAllPlayerWithCardsModelGRPC(
                        AllPlayerWithCardsModel(
                            p.Players
                        )
                    ).playersList
                )
                .setMaxPlayer(p.MaxPlayer)
                .setMaxPlayerDeck(p.MaxPlayerDeck)
                .setMaxDeploment(p.MaxDeploment)
                .setEachPlayerHealth(p.EachPlayerHealth)
                .setCoolDownTime(p.CoolDownTime)
                .addAllCardReward(toAllCardModelGRPC(
                    AllCardModel(p.CardReward)
                ).cardsList)
                .setLevelReward(p.LevelReward)
                .build()
        }
        fun toAllRoomModel(p : CardBattle.allRoom) : AllRoomModel {
            val cards = ArrayList<RoomDataModel>()
            for (i in p.roomsList){
                cards.add(toRoomModel(i))
            }
            return AllRoomModel(cards)
        }
        fun toAllRoomModelGRPC(p : AllRoomModel) : CardBattle.allRoom{
            val rooms = ArrayList<CardBattle.roomData>()
            for (i in p.Rooms){
                rooms.add(toRoomModelGRPC(i))
            }
            return CardBattle.allRoom.newBuilder()
                .addAllRooms(rooms)
                .build()
        }


        // player with deck
        fun toPlayerWithCardsModel(p : CardBattle.playerWithCards) : PlayerWithCardsModel {
            return PlayerWithCardsModel(
                toPlayerModel(p.owner),
                toAllCardModel(
                    CardBattle.allCard.newBuilder()
                        .addAllCards(p.deckList)
                        .build()
                ).Cards,
                toAllCardModel(
                    CardBattle.allCard.newBuilder()
                        .addAllCards(p.reserveList)
                        .build()
                ).Cards,
                toAllCardModel(
                    CardBattle.allCard.newBuilder()
                        .addAllCards(p.deployedList)
                        .build()
                ).Cards,
                p.hp,
                p.attackPower,
                p.damageReceive
            )
        }

        fun toPlayerWithCardsModelGRPC(p : PlayerWithCardsModel) : CardBattle.playerWithCards {
            return CardBattle.playerWithCards.newBuilder()
                .setOwner(toPlayerModelGRPC(p.Owner))
                .addAllDeck(toAllCardModelGRPC(AllCardModel(p.Deck)).cardsList)
                .addAllDeployed(toAllCardModelGRPC(AllCardModel(p.Deployed)).cardsList)
                .addAllReserve(toAllCardModelGRPC(AllCardModel(p.Reserve)).cardsList)
                .setHp(p.Hp)
                .setDamageReceive(p.DamageReceive)
                .setDamageReceive(p.DamageReceive)
                .build()
        }

        fun toAllPlayerWithCardsModel(p : CardBattle.allPlayerWithCards) : AllPlayerWithCardsModel {
            val players = ArrayList<PlayerWithCardsModel>()
            for (i in p.playersList){
                players.add(toPlayerWithCardsModel(i))
            }
            return AllPlayerWithCardsModel(
                players
            )
        }

        fun toAllPlayerWithCardsModelGRPC(p : AllPlayerWithCardsModel) : CardBattle.allPlayerWithCards{
            val players = ArrayList< CardBattle.playerWithCards>()
            for (i in p.Players){
                players.add(toPlayerWithCardsModelGRPC(i))
            }
            return CardBattle.allPlayerWithCards.newBuilder()
                .addAllPlayers(players)
                .build()
        }

    }
}