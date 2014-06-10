package com.github.thomasfischl.akka.sample.cluster;

import java.io.File;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.scan.StandardJarScanner;

public class ServerRunner {

  public static void main(String[] args) throws ServletException, LifecycleException {

    String webappDirLocation = "./target/classes";
    Tomcat tomcat = new Tomcat();
    tomcat.setPort(8080);

    Connector connector = tomcat.getConnector();
    ProtocolHandler protocolHandler = connector.getProtocolHandler();
    if (protocolHandler instanceof Http11NioProtocol) {
      ((Http11NioProtocol) protocolHandler).setMaxThreads(BrainServerConfiguraiton.TOMCAT_WORKER_THEAD_MAX);
      ((Http11NioProtocol) protocolHandler).setMaxConnections(1000);
    }
    tomcat.setConnector(connector);
    StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
    ((StandardJarScanner) ctx.getJarScanner()).setScanAllDirectories(true);
    ((StandardJarScanner) ctx.getJarScanner()).setScanBootstrapClassPath(true);
    ((StandardJarScanner) ctx.getJarScanner()).setScanClassPath(true);

    File additionWebInfClasses = new File("target/classes");
    WebResourceRoot resources = new StandardRoot(ctx);
    resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
    ctx.setResources(resources);

    tomcat.start();
    tomcat.getServer().await();
  }

}
