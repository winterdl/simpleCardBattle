package com.syahputrareno975.cardbattlemodule.model;
import java.io.Serializable;

public class NetworkConfig implements Serializable {
  public String Url = "";
  public int Port = 0;

  public NetworkConfig(String url, int port) {
    Url = url;
    Port = port;
  }

  public String toHttpUrl() {
    return "http://" + this.Url + ":" + this.Port;
  }

}