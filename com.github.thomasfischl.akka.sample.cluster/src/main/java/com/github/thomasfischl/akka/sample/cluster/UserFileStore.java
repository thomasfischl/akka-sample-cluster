package com.github.thomasfischl.akka.sample.cluster;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

public class UserFileStore {

  private File baseStorageDirectory;
  private Object montior = new Object();
  private File currFile;
  private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  private String userid;
  private Random rand = new Random();

  public UserFileStore(File baseStorageDirectory, String userid) {
    this.baseStorageDirectory = baseStorageDirectory;
    this.userid = userid;

    baseStorageDirectory.mkdirs();
  }

  public void storeData(SensorDataGroup group) throws IOException {
    String filename = "data-" + userid + "-" + sdf.format(System.currentTimeMillis()) + ".csv";

    // synchronized (montior) {
    if (currFile != null && !currFile.getName().equalsIgnoreCase(filename)) {
      currFile = null;
    }

    if (currFile == null) {
      currFile = new File(baseStorageDirectory, filename);
    }

    String data = new Gson().toJson(group) + "\r\n";
    FileUtils.write(currFile, data, true);

    processFile();
    // }
  }

  private void processFile() {
    if (BrainServerConfiguraiton.FILE_PROCESS_DELAY_RAND > 0) {
      int sleepTime = rand.nextInt(BrainServerConfiguraiton.FILE_PROCESS_DELAY_RAND);
      try {
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public SensorDataGroup loadData() throws IOException {
    synchronized (montior) {
      // if (currFile != null) {
      // List<String> lines = FileUtils.readLines(currFile);
      // processFile();
      // if (lines.size() > 0) {
      // return new Gson().fromJson(lines.get(lines.size() - 1), SensorDataGroup.class);
      // }
      // }
      return new SensorDataGroup();
    }
  }

}
