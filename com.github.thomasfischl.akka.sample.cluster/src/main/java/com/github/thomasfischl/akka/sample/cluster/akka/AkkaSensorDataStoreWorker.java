package com.github.thomasfischl.akka.sample.cluster.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.github.thomasfischl.akka.sample.cluster.BrainController;
import com.github.thomasfischl.akka.sample.cluster.SensorDataGroup;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataProcessMsg;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataStoreMsg;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaMessages.SensorDataWorResultMsg;

public class AkkaSensorDataStoreWorker extends UntypedActor {

  private BrainController controller = BrainController.getInstance();

  @Override
  public void onReceive(final Object msg) throws Exception {
    if (msg instanceof SensorDataStoreMsg) {

      controller.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          try {
            SensorDataStoreMsg msgObj = (SensorDataStoreMsg) msg;
            controller.storeData(msgObj.getMessage().getUserId(), msgObj.getMessage().getGroup());
          } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
          }
        }
      });

    } else if (msg instanceof SensorDataProcessMsg) {
      final ActorRef actorSelf = getSelf();
      final ActorRef actorSender = getSender();
      controller.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          try {
            SensorDataProcessMsg msgObj = (SensorDataProcessMsg) msg;
            SensorDataGroup result = controller.calcualteDeviceState(msgObj.getMessage().getUserId());
            actorSender.tell(new SensorDataWorResultMsg(msgObj.getMessage().getSessionId(), result), actorSelf);
          } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
          }
        }
      });
    }
  }

}
