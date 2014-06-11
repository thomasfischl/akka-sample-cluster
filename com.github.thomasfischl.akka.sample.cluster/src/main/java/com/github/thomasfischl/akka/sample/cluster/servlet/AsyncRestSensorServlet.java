package com.github.thomasfischl.akka.sample.cluster.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.github.thomasfischl.akka.sample.cluster.MetricService;
import com.github.thomasfischl.akka.sample.cluster.SensorDataGroup;
import com.github.thomasfischl.akka.sample.cluster.akka.AkkaFrontendFacade;
import com.github.thomasfischl.akka.sample.cluster.akka.ResponseHandler;
import com.google.common.base.Strings;
import com.google.gson.Gson;

@WebServlet(name = "AsyncRestSensorServlet", urlPatterns = { "/async/rest/*" }, asyncSupported = true)
public class AsyncRestSensorServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private AkkaFrontendFacade frontend = new AkkaFrontendFacade();
  // private AkkaFrontendFacade frontend = new AkkaClusterFrontendFacade();

  private Timer timer;

  private Meter errorCounter;

  public AsyncRestSensorServlet() {
    timer = MetricService.getInstance().registerTimer("async-response-time");
    errorCounter = MetricService.getInstance().registerMeter("async-errors");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    final AsyncContext asyncCtx = req.startAsync();
    asyncCtx.start(new AsyncHttpRequestHandler(asyncCtx));
  }

  private final class AsyncHttpRequestHandler implements Runnable {
    private final AsyncContext asyncCtx;
    private Context ctx;

    private AsyncHttpRequestHandler(AsyncContext asyncCtx) {
      this.asyncCtx = asyncCtx;
      ctx = timer.time();
    }

    @Override
    public void run() {
      HttpServletRequest req = (HttpServletRequest) asyncCtx.getRequest();
      HttpServletResponse resp = (HttpServletResponse) asyncCtx.getResponse();

      try {
        processRequest(req, resp);
      } catch (IOException e) {
        errorCounter.mark();
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }

    private void processRequest(HttpServletRequest req, final HttpServletResponse resp) throws IOException {
      String userId = req.getPathInfo();

      if (Strings.isNullOrEmpty(userId)) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ServletHelper.printUsage(resp.getWriter());
        asyncCtx.complete();
        ctx.stop();
        return;
      }

      userId = userId.replace("/", "");
      String requestData = IOUtils.toString(req.getInputStream());

      SensorDataGroup group = new Gson().fromJson(requestData, SensorDataGroup.class);
      frontend.processSensorData(userId, group, new ResponseHandler() {
        @Override
        public void process(SensorDataGroup result) {
          try {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            Gson gson = new Gson();
            writer.println(gson.toJson(result));
            writer.flush();
            writer.close();
          } catch (IOException e) {
            errorCounter.mark();
            e.printStackTrace();
            throw new RuntimeException(e);
          } finally {
            asyncCtx.complete();
            ctx.stop();
          }
        }
      });
    }
  }
}
