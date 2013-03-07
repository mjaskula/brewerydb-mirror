package org.jaskula.brewerydbmirror

import scala.concurrent.Future
import play.api.Configuration
import org.jaskula.brewerydbclient.BreweryDbClient
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json
import com.mongodb.util.JSON
import play.api.libs.concurrent.Execution.Implicits._

class DataProcessor(config: Configuration) { //TODO: better name?

  val breweryDbClient = new BreweryDbClient(config.getString("brewerydb.apikey").getOrElse("specify apikey in 'conf/brewerydb.apikey.conf'"))
  
  val mongodb =  MongoClient()(config.getString("mongodb.default.db").getOrElse("test"))
  
  
  def updateBeersForStyle(styleId: String): Future[Int] = {
    breweryDbClient.beersJsonForStyle(styleId).map { beers =>
      beers.map { beerJson =>
        val beer = com.mongodb.util.JSON.parse(Json.stringify(beerJson)).asInstanceOf[DBObject]
        mongodb("beers").update(MongoDBObject("id" -> beer("id")),
                                beer, 
                                upsert = true)
      }
      beers.size
    }
  }
}