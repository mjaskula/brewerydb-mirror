package org.jaskula.brewerydbmirror

import akka.actor.Actor
import akka.actor.ActorRef
import org.jaskula.brewerydbclient.BreweryDbClient
import play.api.libs.concurrent.Execution.Implicits._
import org.jaskula.brewerydbmirror.MessageType._

class BreweryDbReaderActor(breweryDbClient: BreweryDbClient, writer: ActorRef) extends Actor {
  
  def receive = {
    case ReadStyles =>  readAllStyles
    case unsupportedMsg =>
      play.Logger.info("Received unsupported message '%s' in reader actor %s".format(unsupportedMsg, self.path.name))
  }
  
  private def readAllStyles = {
    play.Logger.info("Received message 'styles' in reader actor %s".format(self.path.name))
    
    breweryDbClient.stylesJson().map { styles =>
      styles.map { styleJson =>
        writer ! (WriteStyle, styleJson)
      }
    }
  }
}
