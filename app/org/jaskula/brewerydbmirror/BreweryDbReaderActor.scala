package org.jaskula.brewerydbmirror

import akka.actor.Actor
import akka.actor.ActorRef
import org.jaskula.brewerydbclient.BreweryDbClient
import play.api.libs.concurrent.Execution.Implicits._
import org.jaskula.brewerydbmirror.MessageType._
import org.jaskula.brewerydbclient.BreweryResponse
import scala.concurrent.Future
import org.jboss.logging.LogMessage

class BreweryDbReaderActor(breweryDbClient: BreweryDbClient, writer: ActorRef) extends Actor {
  
  def receive = {
    case ReadAll => readAll
    case ReadStyles => readAllStyles
    case (ReadBeersForStyle, styleId: String) =>  readBeersForStyle(styleId)
    case (ReadBeersForStyle, styleId: String, page: Int) =>  readBeersForStyle(styleId, page)
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
  
  
  private def readBeersForStyle(styleId: String): Future[BreweryResponse] = {
    logMessage(ReadBeersForStyle, "style:" + styleId)
    readBeersForStyle(styleId, 1).map { beersResponse =>
      for (page <- beersResponse.remainingPages()) {
        context.parent ! (ReadBeersForStyle, styleId, page)
      }
      beersResponse
    }
  }
 
  private def readBeersForStyle(styleId: String, page: Int): Future[BreweryResponse] = {
    logMessage(ReadBeersForStyle, "style:" + styleId + ",page:" + page)
    breweryDbClient.beersJsonForStyle(styleId, page).map { beersResponse =>
      beersResponse.data.map { beerJson =>
        writer ! (WriteBeer, beerJson)
      }
      beersResponse
    }
  }
  
  private def logMessage(msgType: MessageType, info: String = "") =
    play.Logger.trace("Received message '%s(%s)' in reader actor %s".format(msgType, info, self.path.name))
}
