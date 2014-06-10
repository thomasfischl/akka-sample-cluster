package com.github.thomasfischl.akka.sample.cluster.akka.cluster;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;

import com.github.thomasfischl.akka.sample.cluster.akka.AkkaFrontendFacade;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaFrontendMaster;

public class AkkaClusterFrontendActor extends AkkaFrontendMaster {

  public AkkaClusterFrontendActor(int nrOfWorkers, AkkaFrontendFacade facade) {
    super(nrOfWorkers, facade);
  }

  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  @Override
  protected void init() {
    workerRouter = getContext().actorOf(FromConfig.getInstance().props(), "factorialBackendRouter");
  }

  @Override
  public void preStart() {
    getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
  }
}
