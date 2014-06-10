package com.github.thomasfischl.akka.sample.cluster;

public class SensorData {

  private String name;

  private String value;

  private String type;

  public SensorData() {
  }

  public SensorData(String name, String value, String type) {
    super();
    this.name = name;
    this.value = value;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "SensorData: [name:" + name + " value: " + value + "]";
  }

}
