package org.jaskula.brewerydbmirror

import play.api.Configuration
import org.jaskula.brewerydbclient.BreweryDbClient
import com.google.inject._
import org.jaskula.brewerydbmirror.MessageType._

@Singleton  //TODO: better name?
class DataProcessor @Inject()(config: Configuration,
                              breweryDbClient: BreweryDbClient,
                              actorPool: ActorPool) {

  def updateStyles() = {  
    actorPool.reader ! ReadStyles
  }
  
  def updateBeers() = {
    actorPool.reader ! ReadBeers
  }
}