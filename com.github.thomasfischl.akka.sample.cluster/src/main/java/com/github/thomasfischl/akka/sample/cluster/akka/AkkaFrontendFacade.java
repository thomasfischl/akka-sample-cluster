package com.github.thomasfischl.akka.sample.cluster.akka;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.codahale.metrics.Counter;
import com.github.thomasfischl.akka.sample.cluster.BrainServerConfiguraiton;
import com.github.thomasfischl.akka.sample.cluster.MetricService;
import com.github.thomasfischl.akka.sample.cluster.SensorDataGroup;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataWorkMsg;

public class AkkaFrontendFacade {

  protected ActorSystem system;

  protected ActorRef frontendMaster;

  private AtomicLong counter = new AtomicLong();

  private Map<Long, ResponseHandler> responseCallbacks = new HashMap<>();

  private Counter backlogCounter;

  public AkkaFrontendFacade() {
    init();
    backlogCounter = MetricService.getInstance().registerCounter("async-backlog");
  }

  protected void init() {
    system = ActorSystem.create("BrainServer");
    frontendMaster = system.actorOf(Props.create(AkkaFrontendMaster.class, BrainServerConfiguraiton.AKKA_WORKERS_MAX, this), "frontendMaster");
  }

  public void processSensorData(String userId, SensorDataGroup group, ResponseHandler responseHandler) {
    long sessionId = counter.getAndIncrement();
    responseCallbacks.put(sessionId, responseHandler);
    backlogCounter.inc();
    frontendMaster.tell(new SensorDataWorkMsg(sessionId, userId, group), ActorRef.noSender());
  }

  public void finishRequest(long sessionId, SensorDataGroup result) {
    if (responseCallbacks.containsKey(sessionId)) {
      backlogCounter.dec();
      ResponseHandler handler = responseCallbacks.remove(sessionId);
      handler.process(result);
    } else {
      System.err.println("No response handler for session id '" + sessionId + "' available.");
    }
  }

}
