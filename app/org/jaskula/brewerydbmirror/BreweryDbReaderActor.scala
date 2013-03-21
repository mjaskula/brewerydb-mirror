package org.jaskula.brewerydbmirror

import akka.actor.Actor
import akka.actor.ActorRef
import org.jaskula.brewerydbclient.BreweryDbClient
import play.api.libs.concurrent.Execution.Implicits._
import org.jaskula.brewerydbmirror.MessageType._

class BreweryDbReaderActor(breweryDbClient: BreweryDbClient, writer: ActorRef) extends Actor {
  
  def receive = {
    case ReadAll => readAll
    case ReadStyles => readAllStyles
    case (ReadBeersForStyle, styleId: String) =>  readBeersForStyle(styleId)
    case unsupportedMsg =>
      play.Logger.info("Received unsupported message '%s' in reader actor %s".format(unsupportedMsg, self.path.name))
  }
  
  private def readAll(): Unit = {
    logMessage(ReadAll)
    readAllStyles(withBeers = true)
  }
  
  private def readAllStyles(): Unit = {
    logMessage(ReadStyles)
    readAllStyles(withBeers = false)
  }
  
  private def readAllStyles(withBeers: Boolean): Unit = {
    breweryDbClient.stylesJson().map { stylesResponse =>
      stylesResponse.data.map { styleJson =>
        writer ! (WriteStyle, styleJson)
        if (withBeers) {
          context.parent ! (ReadBeersForStyle, (styleJson \ "id").toString)
        }
      }
    }
  }
  
  
  private def readBeersForStyle(styleId: String) = {
    breweryDbClient.beersJsonForStyle(styleId).map { beersResponse =>
      beersResponse.data.map { beerJson =>
        writer ! (WriteBeer, beerJson)
      }
    }
  }
  
  private def logMessage(msgType: MessageType) =
    play.Logger.trace("Received message '%s' in reader actor %s".format(msgType, self.path.name))
}
