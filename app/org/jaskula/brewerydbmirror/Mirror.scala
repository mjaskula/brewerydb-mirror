package org.jaskula.brewerydbmirror

import com.google.inject._
import org.jaskula.brewerydbmirror.MessageType._
import org.jaskula.brewerydbclient.StatsStorageProvider

@Singleton
class Mirror @Inject()(actorPool: ActorPool,
                       stats: StatsStorageProvider) {

  def loadAll() = { //TODO: should read styles, then beers
    stats.logCommand("loadAll")
    actorPool.reader ! ReadStyles
    actorPool.reader ! ReadBeers
  }

  def loadAllStyles() = {
    stats.logCommand("loadAllStyles")
    actorPool.reader ! ReadStyles
  }
  
  def loadAllBeers() = {
    stats.logCommand("loadAllBeers")
    actorPool.reader ! ReadBeers
  }
}