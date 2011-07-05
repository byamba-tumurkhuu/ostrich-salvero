package org.ostrich.salvero.core

import grizzled.slf4j.Logging
import com.twitter.ostrich.admin._
import com.twitter.ostrich.admin.config._
import org.salvero.core.{Push, Connect}

object OstrichSalveroClient {

  def apply(ip: String, adminConfig: AdminServiceConfig, runtime: RuntimeEnvironment) = 
    new OstrichSalveroClient(ip, adminConfig, runtime) 

  def apply(ip: String) = {

    // Ostrich setup
    val adminConfig = new AdminServiceConfig {
      httpPort = 9991
      statsNodes = new StatsConfig { 
        reporters = new TimeSeriesCollectorConfig 
      }
    }

    new OstrichSalveroClient(ip, adminConfig, RuntimeEnvironment(this, Array[String]()))
  }

}


class OstrichSalveroClient(ip: String, adminConfig: AdminServiceConfig, runtime: RuntimeEnvironment) {

  def connect() {

    val push = Some(new Push(ip) with Connect)
    val admin = adminConfig()(runtime)

    admin map (_.remote = new SalveroRemote(push)) // Hook the implementation into Ostrich

  }

}


/*
** Hook implementation
*/
case class SalveroRemote(push: Option[Push]) extends Remote with Logging {
  override def time(name: String, value: Int) = {
    debug("Sending => name: " + name + ", value: " + value)
    push map (_ ! AddMetric(name, value))
  }
}
