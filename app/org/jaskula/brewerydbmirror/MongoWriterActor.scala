package org.jaskula.brewerydbmirror

import akka.actor.Actor
import akka.actor.ActorRef
import com.mongodb.casbah.MongoClient
import play.api.Configuration
import play.api.libs.json.Json
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import org.jaskula.brewerydbmirror.MessageType._
import org.joda.time.format.DateTimeFormat
import com.mongodb.casbah.commons.conversions.scala._
import org.jaskula.brewerydbmirror.MongoService

class MongoWriterActor(mongo: MongoService) extends Actor {
  
  RegisterJodaTimeConversionHelpers()
  
  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd kk:mm:ss");
  
  def receive = {
    case (WriteStyle,     styleJson: JsValue) => saveStyle(styleJson)
    case (WriteBeer,       beerJson: JsValue) => saveBeer(beerJson)
    case unsupportedMsg =>
      play.Logger.info("Received unsupported message '%s' in writer actor %s".format(unsupportedMsg, self.path.name))
  }
  
  private def saveStyle(styleJson: JsValue) = {
    logMessage(WriteStyle)
    // TODO: should style categories be their own collection?
    upsertById(mongo.styles, com.mongodb.util.JSON.parse(Json.stringify(styleJson)).asInstanceOf[DBObject])
  }
  
  private def saveBeer(beerJson: JsValue) = {
    logMessage(WriteBeer)
    val beer = com.mongodb.util.JSON.parse(Json.stringify(beerJson)).asInstanceOf[DBObject]
    beer.removeField("style")
    saveBreweryFromBeerData(beer)
    upsertById(mongo.beers, processFields(beer))
  }
  
  def saveBreweryFromBeerData(beer: DBObject): Int = {
    val breweryList = beer.getAsOrElse[MongoDBList]("breweries", MongoDBList.empty)
    var count = 0
    beer.removeField("breweries") //TODO: brewery ids
    val breweryIdList = MongoDBList.newBuilder
    breweryList.map { brewery =>
      breweryIdList += brewery.asInstanceOf[DBObject].get("id")
      count += upsertById(mongo.breweries, processFields(brewery.asInstanceOf[DBObject]))
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
  
  private def logMessage(msgType: MessageType) =
    play.Logger.info("Received message '%s' in writer actor %s".format(msgType, self.path.name))
  
  private def upsertById(coll: MongoCollection, data: DBObject): Int = {
    play.Logger.info("Saving to %s: %s".format(coll.name, data("name")))
    coll.update(MongoDBObject("id" -> data("id")), data, upsert = true).getN()
  }
}