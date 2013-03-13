package org.jaskula.brewerydbclient

import play.api.Configuration
import com.mongodb.casbah.query.Imports._
import org.joda.time.LocalDate
import org.joda.time.DateTimeZone
import com.mongodb.casbah.MongoClient
import com.google.inject._
import org.joda.time.DateTime
import org.jaskula.brewerydbmirror.MongoService
import com.mongodb.casbah.commons.MongoDBObject

@Singleton
class StatsStorageProvider @Inject()(mongo: MongoService) {
  
  val fieldCount = "count"
  val fieldDate = "date"
  val fieldCommand = "command"
  
  def countApiCall(): Unit = {
    mongo.apiCalls.update(MongoDBObject(fieldDate -> today), 
                          $inc(fieldCount -> 1), 
                          upsert = true)
  }
  
  def getTodaysApiCallCount(): Int = {
    mongo.apiCalls.findOne(MongoDBObject(fieldDate -> today)) match {
      case Some(result) => result.getAsOrElse[Int](fieldCount, 0)
      case None => 0
    }
  }
  
  def logCommand(command: String): Unit = {
    mongo.commandLog.insert(MongoDBObject(fieldDate -> DateTime.now(), fieldCommand -> command))
  }
  
  def getRecentCommands(count: Int = 5): Iterator[(String, String)] = {
    for (log <- mongo.commandLog.find().limit(count).sort(MongoDBObject(fieldDate -> 1))) yield {
      (log.getAsOrElse[String](fieldDate, "unknown Date"), log.getAsOrElse[String](fieldCommand, "Unknown Command"))
    } 
  }
  
  private def today: DateTime = LocalDate.now(DateTimeZone.forID("America/New_York")).toDateTimeAtStartOfDay()  //BreweryDB Time zone
}