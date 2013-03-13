package org.jaskula.brewerydbmirror

import com.google.inject._
import org.jaskula.brewerydbmirror.MessageType._

@Singleton  //TODO: better name?
class DataProcessor @Inject()(actorPool: ActorPool) {

  def loadAllStyles() = {  
    actorPool.reader ! ReadStyles
  }
  
  def loadAllBeers() = {
    actorPool.reader ! ReadBeers
  }
}