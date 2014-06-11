package com.github.thomasfischl.akka.sample.cluster.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;

import com.github.thomasfischl.akka.sample.cluster.SensorData;
import com.github.thomasfischl.akka.sample.cluster.SensorDataGroup;
import com.google.gson.Gson;

public class ServletHelper {

  public static void printUsage(PrintWriter writer) {
    writer.println("The userid is missing. E.g. http://localhost:8080/rest/001");

    SensorDataGroup group = new SensorDataGroup(new ArrayList<SensorData>(), System.currentTimeMillis());
    group.getValues().add(new SensorData("relay001", "true", "bool"));
    group.getValues().add(new SensorData("led001", "true", "bool"));
    group.getValues().add(new SensorData("led002", "false", "bool"));

    writer.println("Data: " + new Gson().toJson(group));
  }
  
}
