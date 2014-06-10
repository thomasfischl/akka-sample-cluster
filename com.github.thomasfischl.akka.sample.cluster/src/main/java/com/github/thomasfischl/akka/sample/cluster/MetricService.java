package com.github.thomasfischl.akka.sample.cluster;

import com.codahale.metrics.Counter;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class MetricService {

  private final MetricRegistry metrics = new MetricRegistry();

  private static MetricService singleton;

  private JmxReporter reporter;

  private MetricService() {
    reporter = JmxReporter.forRegistry(metrics).build();
    reporter.start();
  }

  public Meter registerMeter(String name) {
    return metrics.meter(name);
  }

  public Timer registerTimer(String name) {
    return metrics.timer(name);
  }

  public Counter registerCounter(String name) {
    return metrics.counter(name);
  }

  public static MetricService getInstance() {
    if (singleton == null) {
      singleton = new MetricService();
    }
    return singleton;
  }
}
