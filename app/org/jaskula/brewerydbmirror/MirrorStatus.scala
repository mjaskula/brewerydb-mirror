package org.jaskula.brewerydbmirror

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import javax.inject.Inject
import javax.inject.Singleton
import org.jaskula.brewerydbclient.StatsStorageProvider

@Singleton
class MirrorStatus @Inject()(mongo: MongoService,
                             stats: StatsStorageProvider) {
  
  def status(): Future[Status] = {
    Future {
      new Status(mongo.styles.size,
                 mongo.breweries.size,
                 mongo.beers.size,
                 stats.getRecentCommands(),
                 stats.getTodaysApiCallCount
                 )
    }
  }
  
  /**
   * A container for counts
   */
  case class Status(styles: Int,
                    breweries: Int,
                    beers: Int,
                    commands: Iterator[(String, String)],
                    apiCalls: Int) {
    
    override def toString() = {
      "Styles: " + styles +
      "\nBreweries: " + breweries +
      "\nBeers: " + beers +
      "\n\nRecentCommands:" + commands.foldLeft("")(_+"\n"+_) +
      "\n\nAPI Calls Today: " + apiCalls 
    }
  }
}
