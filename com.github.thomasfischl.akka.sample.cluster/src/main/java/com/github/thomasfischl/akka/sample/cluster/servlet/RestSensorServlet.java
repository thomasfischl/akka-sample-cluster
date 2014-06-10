package com.github.thomasfischl.akka.sample.cluster.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.github.thomasfischl.akka.sample.cluster.BrainController;
import com.github.thomasfischl.akka.sample.cluster.MetricService;
import com.github.thomasfischl.akka.sample.cluster.SensorDataGroup;
import com.google.common.base.Strings;
import com.google.gson.Gson;

@WebServlet(name = "RestSensorServlet", urlPatterns = { "/rest/*" })
public class RestSensorServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private BrainController controller = BrainController.getInstance();

  private Timer timer;

  public RestSensorServlet() {
    timer = MetricService.getInstance().registerTimer("sync-response-time");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Context ctx = timer.time();
    String userId = req.getPathInfo();

    if (Strings.isNullOrEmpty(userId)) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      PrintWriter writer = resp.getWriter();
      writer.println("The userid is missing. E.g. http://localhost:8080/rest/001");
      return;
    }

    userId = userId.replace("/", "");

    SensorDataGroup result = processRequest(req, userId);

    resp.setStatus(HttpServletResponse.SC_CREATED);
    resp.setContentType("application/json");
    PrintWriter writer = resp.getWriter();
    try {
      Gson gson = new Gson();
      writer.println(gson.toJson(result));
      writer.flush();
    } finally {
      writer.close();
      ctx.close();
    }

  }

  private SensorDataGroup processRequest(HttpServletRequest req, String userId) throws IOException {
    String requestData = IOUtils.toString(req.getInputStream());
    SensorDataGroup group = new Gson().fromJson(requestData, SensorDataGroup.class);
    controller.storeData(userId, group);
    SensorDataGroup result = controller.calcualteDeviceState(userId);
    return result;
  }
}
