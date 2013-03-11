package org.jaskula.brewerydbmirror

import akka.actor.Actor
import akka.actor.ActorRef
import org.jaskula.brewerydbclient.BreweryDbClient
import play.api.libs.concurrent.Execution.Implicits._

class BreweryDbReaderActor(breweryDbClient: BreweryDbClient, writer: ActorRef) extends Actor {
  
  def receive = {
    case "styles" =>
      play.Logger.info("Received message 'styles' in reader actor %s".format(self.path.name))
      
      breweryDbClient.stylesJson().map { styles =>
        styles.map { styleJson =>
          writer ! ("style", styleJson)
        }
      }

    case msg =>
      play.Logger.info("Received unsupported message '%s' in reader actor %s".format(msg, self.path.name))
  }
}
