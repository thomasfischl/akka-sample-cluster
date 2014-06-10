package com.github.thomasfischl.akka.sample.cluster.akka;

import com.github.thomasfischl.akka.sample.cluster.SensorDataGroup;

public class AkkaMessages {

  public static class SensorDataWorkMsg {

    private final SensorDataGroup group;

    private final String userId;

    private final long sessionId;

    public SensorDataWorkMsg(long sessionId, String userId, SensorDataGroup group) {
      this.sessionId = sessionId;
      this.userId = userId;
      this.group = group;
    }

    public SensorDataGroup getGroup() {
      return group;
    }

    public String getUserId() {
      return userId;
    }

    public long getSessionId() {
      return sessionId;
    }
  }

  public static class SensorDataWorResultMsg {

    private final SensorDataGroup group;
    private final long sessionId;

    public SensorDataWorResultMsg(long sessionId, SensorDataGroup group) {
      this.sessionId = sessionId;

      this.group = group;
    }

    public SensorDataGroup getGroup() {
      return group;
    }

    public long getSessionId() {
      return sessionId;
    }

  }

  public static class SensorDataStoreMsg {

    private final SensorDataWorkMsg message;

    public SensorDataStoreMsg(SensorDataWorkMsg msg) {
      this.message = msg;
    }

    public SensorDataWorkMsg getMessage() {
      return message;
    }

  }

  public static class SensorDataProcessMsg {

    private final SensorDataWorkMsg message;

    public SensorDataProcessMsg(SensorDataWorkMsg msg) {
      this.message = msg;
    }

    public SensorDataWorkMsg getMessage() {
      return message;
    }
  }

}
