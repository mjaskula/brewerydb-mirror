package org.jaskula.brewerydbmirror

import akka.actor.Actor
import akka.actor.ActorRef
import org.jaskula.brewerydbclient.BreweryDbClient
import play.api.libs.concurrent.Execution.Implicits._
import org.jaskula.brewerydbmirror.MessageType._

class BreweryDbReaderActor(breweryDbClient: BreweryDbClient, writer: ActorRef) extends Actor {
  
  def receive = {
    case ReadStyles => readAllStyles
    case (ReadBeersForStyle, styleId: String) =>  readBeersForStyle(styleId)
    case unsupportedMsg =>
      play.Logger.info("Received unsupported message '%s' in reader actor %s".format(unsupportedMsg, self.path.name))
  }
  
  private def readAllStyles = {
    logMessage(ReadStyles)
    
    breweryDbClient.stylesJson().map { styles =>
      styles.map { styleJson =>
        writer ! (WriteStyle, styleJson)
      }
    }
  }
  
  
  private def readBeersForStyle(styleId: String) = {
    breweryDbClient.beersJsonForStyle(styleId).map { beers =>
      beers.map { beerJson =>
        writer ! (WriteBeer, beerJson)
      }
    }
  }
  
  private def logMessage(msgType: MessageType) =
    play.Logger.info("Received message '%s' in reader actor %s".format(msgType, self.path.name))
}
