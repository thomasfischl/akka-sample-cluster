package com.github.thomasfischl.akka.sample.cluster.akka;

import com.github.thomasfischl.akka.sample.cluster.SensorDataGroup;

public interface ResponseHandler {

  void process(SensorDataGroup data);

}
