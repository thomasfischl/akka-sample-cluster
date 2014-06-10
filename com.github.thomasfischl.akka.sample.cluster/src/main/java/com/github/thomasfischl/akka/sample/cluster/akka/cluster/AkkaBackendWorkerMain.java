package com.github.thomasfischl.akka.sample.cluster.akka.cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;

import com.github.thomasfischl.akka.sample.cluster.akka.AkkaSensorDataStoreWorker;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaBackendWorkerMain {

  public static void main(String[] args) {
    // Override the configuration of the port when specified as program argument
    final String port = args.length > 0 ? args[0] : "2551";
    final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).withFallback(ConfigFactory.load("factorial"));

    ActorSystem system = ActorSystem.create("ClusterSystem", config);
    system.actorOf(Props.create(AkkaSensorDataStoreWorker.class), "factorialBackend");
    system.actorOf(Props.create(MetricsListener.class), "metricsListener");
  }

}
