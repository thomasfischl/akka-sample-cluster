package com.github.thomasfischl.akka.sample.cluster.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataProcessMsg;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataStoreMsg;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataWorResultMsg;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataWorkMsg;

public class AkkaFrontendMaster extends UntypedActor {

  // private int nrOfWorkers;
  private ActorRef workerRouter;
  private AkkaFrontendFacade facade;

  public AkkaFrontendMaster(int nrOfWorkers, AkkaFrontendFacade facade) {
    // this.nrOfWorkers = nrOfWorkers;

    this.facade = facade;
    workerRouter = this.getContext().actorOf(new Props(AkkaSensorDataStoreWorker.class).withRouter(new RoundRobinRouter(nrOfWorkers)),
        "workerRouter");
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    if (msg instanceof SensorDataWorkMsg) {
      workerRouter.tell(new SensorDataStoreMsg((SensorDataWorkMsg) msg), getSelf());
      workerRouter.tell(new SensorDataProcessMsg((SensorDataWorkMsg) msg), getSelf());
    } else if (msg instanceof SensorDataWorResultMsg) {
      SensorDataWorResultMsg msgObj = (SensorDataWorResultMsg) msg;
      facade.finishRequest(msgObj.getSessionId(), msgObj.getGroup());
    }
  }

}
