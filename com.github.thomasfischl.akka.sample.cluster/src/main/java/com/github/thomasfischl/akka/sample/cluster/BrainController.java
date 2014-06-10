package com.github.thomasfischl.akka.sample.cluster;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrainController {

  private static BrainController singleton;

  private Map<String, UserFileStore> userFileStore = new HashMap<>();

  private File baseStorageDirectory = new File("./store");

  private Random rand = new Random();

  private ExecutorService threadPool;

  private BrainController() {
    threadPool = Executors.newFixedThreadPool(BrainServerConfiguraiton.AKKA_WORKERS_MAX);
  }

  public void storeData(String userid, SensorDataGroup group) throws IOException {
    getUserFileStore(userid).storeData(group);
  }

  public SensorDataGroup calcualteDeviceState(String userid) throws IOException {
    SensorDataGroup data = getUserFileStore(userid).loadData();
    processData();
    return data;
  }

  private void processData() {
    if (BrainServerConfiguraiton.CONTROLLER_PROCESS_DELAY_RAND > 0) {
      int sleepTime = rand.nextInt(BrainServerConfiguraiton.CONTROLLER_PROCESS_DELAY_RAND);
      try {
        Thread.sleep(BrainServerConfiguraiton.CONTROLLER_PROCESS_DELAY_MIN + sleepTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private UserFileStore getUserFileStore(String userid) {
    synchronized (userFileStore) {
      if (!userFileStore.containsKey(userid)) {
        userFileStore.put(userid, new UserFileStore(new File(baseStorageDirectory, "store-" + userid), userid));
      }
    }

    UserFileStore store = userFileStore.get(userid);
    return store;
  }

  public synchronized static BrainController getInstance() {
    if (singleton == null) {
      singleton = new BrainController();
    }
    return singleton;
  }

  public ExecutorService getThreadPool() {
    return threadPool;
  }

}
