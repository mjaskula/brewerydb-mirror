package org.jaskula.brewerydbmirror

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import org.jaskula.brewerydbclient.BreweryDbClient
import play.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json
import com.mongodb.util.JSON
import com.mongodb.casbah.commons.MongoDBObject

object Application extends Controller {
  
  val config = play.api.Play.configuration
  val breweryDbClient = new BreweryDbClient(config.getString("brewerydb.apikey").getOrElse("specify apikey in 'conf/brewerydb.apikey.conf'"))
  
  val mongodb =  MongoClient()(config.getString("mongodb.default.db").getOrElse("test"))
  
  def index = Action {
    Ok("index")
  }
  
  def beers = Action {
	Ok("beers")
  }

  def populate() = Action {
  AsyncResult {
    breweryDbClient.beersJsonForStyle("25").map { beers =>
      beers.map { beerJson =>
        val beer = com.mongodb.util.JSON.parse(Json.stringify(beerJson)).asInstanceOf[DBObject]
        mongodb("beers").update(MongoDBObject("id" -> beer("id")),
                                beer, 
                                upsert = true)
      }
      Ok("crap!")
    }
  }  
}
}