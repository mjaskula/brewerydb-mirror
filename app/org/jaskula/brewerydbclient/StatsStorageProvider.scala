package org.jaskula.brewerydbclient

import play.api.Configuration
import com.mongodb.casbah.query.Imports._
import org.joda.time.LocalDate
import org.joda.time.DateTimeZone
import com.mongodb.casbah.MongoClient
import com.google.inject._
import org.joda.time.DateTime
import org.jaskula.brewerydbmirror.MongoService

trait StatsStorageProvider {
  def countApiCall(): Unit
  def getTodaysApiCallCount(): Int
}

@Singleton
class MongoStatsStorageProvider @Inject()(mongo: MongoService) extends StatsStorageProvider {
  
  val count = "count"
  val date = "date"
  
  override def countApiCall(): Unit = {
    mongo.apiCalls.update(MongoDBObject(date -> today), 
                          $inc(count -> 1), 
                          upsert = true)
  }
  
  override def getTodaysApiCallCount(): Int = {
    mongo.apiCalls.findOne(MongoDBObject(date -> today)) match {
      case Some(result) => result.getAsOrElse[Int](count, 0)
      case None => 0
    }
  }
  
  private def today: DateTime = {
    LocalDate.now(DateTimeZone.forID("America/New_York")).toDateTimeAtStartOfDay()  //BreweryDB Time zone
  }
}