package com.syahputrareno975.simplecardbattle.interfaces

import com.syahputrareno975.simplecardbattle.model.player.PlayerModel

interface QueueStreamEventController {
    fun leftWaitingRoom(p : PlayerModel)
    fun goToBattle()
}