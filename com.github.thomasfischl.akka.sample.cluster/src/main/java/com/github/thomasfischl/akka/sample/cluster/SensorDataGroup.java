package com.github.thomasfischl.akka.sample.cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;

public class SensorDataGroup implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<SensorData> values = new ArrayList<SensorData>();

  private long timestamp;

  public SensorDataGroup() {
  }

  public SensorDataGroup(Collection<SensorData> values, long timestamp) {
    this.timestamp = timestamp;
    this.values = new ArrayList<SensorData>(values);
  }

  public List<SensorData> getValues() {
    return values;
  }

  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return "SensorDataGroup: " + Joiner.on(",").join(values);
  }
}
