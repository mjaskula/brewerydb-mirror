package org.jaskula.brewerydbmirror

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import com.google.inject.Inject
import com.google.inject.Singleton
import org.jaskula.brewerydbclient.StatsStorageProvider

@Singleton
class MirrorStatus @Inject()(mongo: MongoService,
                             stats: StatsStorageProvider) {
  
  def status(): Future[Status] = {
    Future {
      new Status(mongo.styles.size,
                 mongo.breweries.size,
                 mongo.beers.size,
                 stats.getTodaysApiCallCount)
    }
  }
  
  /**
   * A container for counts
   */
  case class Status(styles: Int,
                    breweries: Int,
                    beers: Int,
                    apiCalls: Int) {
    
    override def toString() = {
      "Styles: " + styles +
      "\nBreweries: " + breweries +
      "\nBeers: " + beers +
      "\n\nAPI Calls Today: " + apiCalls 
    }
  }
}
