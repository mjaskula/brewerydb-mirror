package org.jaskula.brewerydbmirror

import scala.concurrent.Future
import play.api.Configuration
import org.jaskula.brewerydbclient.BreweryDbClient
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json
import com.mongodb.util.JSON
import play.api.libs.concurrent.Execution.Implicits._
import com.mongodb.WriteResult
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import com.mongodb.casbah.commons.conversions.scala._
import org.jaskula.brewerydbclient.MongoStatsStorageProvider
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.Promise
import com.google.inject._

@Singleton
class DataProcessor @Inject()(config: Configuration) { //TODO: better name?

  RegisterJodaTimeConversionHelpers()

  val stats = new MongoStatsStorageProvider(config)
  
  val breweryDbClient = new BreweryDbClient(config.getString("brewerydb.apikey").getOrElse("specify apikey in 'conf/brewerydb.apikey.conf'"), stats)
  
  val mongodb =  MongoClient()(config.getString("mongodb.default.db").getOrElse("test"))
  
  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd kk:mm:ss"); 

  val writerRouter = Akka.system.actorOf(Props[MongoWriterActor].
          withRouter(SmallestMailboxRouter(5)), "writerRouter")

  val readerRouter = Akka.system.actorOf(Props(new BreweryDbReaderActor(writerRouter)).
      withRouter(SmallestMailboxRouter(5)), "readerRouter")

  implicit val timeout = Timeout(5 seconds)
  
  def updateStyles(): Future[Int] = { // TODO: should style categories be their own collection? 
    (readerRouter ? "Styles").asInstanceOf[Promise[Int]].future
    
//    breweryDbClient.stylesJson().map { styles =>
//      var count = 0
//      styles.map { styleJson =>
//        count += upsertById("styles", com.mongodb.util.JSON.parse(Json.stringify(styleJson)).asInstanceOf[DBObject])
//      }
//      count
//    }
    
//    scala.concurrent.future {0}
  }
  
  def updateBeersForStyle(styleId: String): Future[UpdateStats] = {
    breweryDbClient.beersJsonForStyle(styleId).map { beers =>
      var stats = newStats()
      beers.map { beerJson =>
        val beer = com.mongodb.util.JSON.parse(Json.stringify(beerJson)).asInstanceOf[DBObject]
        beer.removeField("style")
        stats = stats.incBreweries(saveBreweryFromBeerData(beer))
        stats = stats.incBeers(upsertById("beers", processFields(beer)))
      }
      stats
    }
  }
  
  def saveBreweryFromBeerData(beer: DBObject): Int = {
    val breweryList = beer.getAsOrElse[MongoDBList]("breweries", MongoDBList.empty)
    var count = 0
    beer.removeField("breweries") //TODO: brewery ids
    val breweryIdList = MongoDBList.newBuilder
    breweryList.map { brewery =>
      breweryIdList += brewery.asInstanceOf[DBObject].get("id")
      count += upsertById("breweries", processFields(brewery.asInstanceOf[DBObject]))
    }
    beer.put("breweryIds", breweryIdList.result)
    count
  }

  def processFields(o: DBObject): DBObject = {
    o.getAs[String]("createDate").map { dateString => 
      o.put("createDate", formatter.parseDateTime(dateString))
    }
    o
  }
  
  def upsertById(collName: String, data: DBObject): Int = {
    play.Logger.info("Saving to %s: %s".format(collName, data("name")))
    mongodb(collName).update(MongoDBObject("id" -> data("id")), data, upsert = true).getN()
  }
    
  def newStats() = UpdateStats(0, 0)
    
  case class UpdateStats(beerCount: Int, breweryCount: Int) {
    override def toString = beerCount + " beers, " + breweryCount + " breweries"
    def incBeers(i:Int) = UpdateStats(this.beerCount + i, this.breweryCount)
    def incBreweries(i:Int) = UpdateStats(this.beerCount, this.breweryCount + i)
  }
}