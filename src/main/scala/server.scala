package org.ostrich.salvero.core

import grizzled.slf4j.Logging
import com.twitter.ostrich.admin._
import com.twitter.ostrich.admin.config._
import com.twitter.ostrich.stats._
import org.salvero.core.{Send, CaseClass, Pull, Bind}


object OstrichSalveroServer {

  def apply(ip: String, adminConfig: AdminServiceConfig, runtime: RuntimeEnvironment) =
    new OstrichSalveroServer(ip, adminConfig, runtime) 

  def apply(ip: String) = {

    // Ostrich setup
    val adminConfig = new AdminServiceConfig {
      httpPort = 9990
      statsNodes = new StatsConfig { 
        reporters = new TimeSeriesCollectorConfig 
      }
    }

    new OstrichSalveroServer(ip, adminConfig, RuntimeEnvironment(this, Array[String]()))
  }

}


class OstrichSalveroServer(ip: String, adminConfig: AdminServiceConfig, runtime: RuntimeEnvironment) extends Logging {

  val admin = setupOstrich
  var pull: Option[Pull] = None 

  def setupOstrich = { adminConfig()(runtime) }

  // Start receiving messages
  def start() {

    // handlers extend the Send trait and the ! method receives the message
    val handler = new Send {
      def ![A <: CaseClass: Manifest](msg: A) = msg match {
        case AddMetric(name, value) => {
          debug("Received => Name: " + name + " Value: " + value)
          Stats.addMetric(name, value) 
        }
        case _ => warn("Uncatched case class received")
      }
    }

    pull = Some(new Pull(ip, handler) with Bind)
    pull map (p => new Thread(p).start )
  }

  // Stop receiving messages
  def stop() { pull map (_.stop()) }


}
