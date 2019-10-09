package com.syahputrareno975.simplecardbattle.model

import java.io.Serializable

class NetworkConfig : Serializable {
  var Url = ""
  var Port = 0

  constructor(Url: String, Port: Int) {
    this.Url = Url
    this.Port = Port
  }
}