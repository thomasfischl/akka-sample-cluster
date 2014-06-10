package com.github.thomasfischl.akka.sample.cluster.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;

import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataProcessMsg;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataStoreMsg;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataWorResultMsg;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataWorkMsg;

public class AkkaFrontendMaster extends UntypedActor {

  protected ActorRef workerRouter;
  private AkkaFrontendFacade facade;
  private int nrOfWorkers;

  public AkkaFrontendMaster(int nrOfWorkers, AkkaFrontendFacade facade) {
    this.facade = facade;
    this.nrOfWorkers = nrOfWorkers;
    init();
  }

  protected void init() {
    workerRouter = this.getContext().actorOf(Props.create(AkkaSensorDataStoreWorker.class).withRouter(new RoundRobinPool(nrOfWorkers)), "workerRouter");
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
