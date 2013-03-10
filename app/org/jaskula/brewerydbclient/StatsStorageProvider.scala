package org.jaskula.brewerydbclient

import play.api.Configuration
import com.mongodb.casbah.query.Imports._
import org.joda.time.LocalDate
import org.joda.time.DateTimeZone
import com.mongodb.casbah.MongoClient
import com.google.inject._

trait StatsStorageProvider {
  def countApiCall(): Unit
}

@Singleton
class MongoStatsStorageProvider @Inject()(config: Configuration) extends StatsStorageProvider {
  
  val mongodb =  MongoClient()(config.getString("mongodb.default.db").getOrElse("test"))
  
  override def countApiCall(): Unit = {
    val today = LocalDate.now(DateTimeZone.forID("America/New_York")).toDateTimeAtStartOfDay() //BreweryDB Time zone 
    // db.crap.update({"date": ISODate("2013-05-09")}, {$inc: {"count": 1}}, true)
    val a = $inc("count" -> 1)
    mongodb("apiCalls").update(MongoDBObject("date" -> today), 
                               $inc("count" -> 1), 
                               upsert = true)
  }
}