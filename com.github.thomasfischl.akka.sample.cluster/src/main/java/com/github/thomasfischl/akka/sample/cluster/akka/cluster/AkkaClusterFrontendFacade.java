package com.github.thomasfischl.akka.sample.cluster.akka.cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;

import com.github.thomasfischl.akka.sample.cluster.BrainServerConfiguraiton;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaFrontendFacade;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaClusterFrontendFacade extends AkkaFrontendFacade {

  @Override
  protected void init() {
    Config config = ConfigFactory.parseString("akka.cluster.roles = [frontend]").withFallback(ConfigFactory.load("cluster"));
    system = ActorSystem.create("ClusterSystem", config);
    frontendMaster = system.actorOf(Props.create(AkkaClusterFrontendActor.class, BrainServerConfiguraiton.AKKA_WORKERS_MAX, this), "sensorFrontend");
  }

}
