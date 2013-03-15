package org.jaskula.brewerydbmirror

import com.google.inject._
import org.jaskula.brewerydbmirror.MessageType._
import org.jaskula.brewerydbclient.StatsStorageProvider

@Singleton
class Mirror @Inject()(actorPool: ActorPool,
                       stats: StatsStorageProvider) {

  def loadAll() = {
    stats.logCommand("loadAll")
    actorPool.reader ! ReadAll
  }

  def loadAllStyles() = {
    stats.logCommand("loadAllStyles")
    actorPool.reader ! ReadStyles
  }
  
  def loadBeersForStyle(styleId: String) = {
    stats.logCommand("loadBeersForStyle: " + styleId)
    actorPool.reader ! (ReadBeersForStyle, styleId)
  }
}