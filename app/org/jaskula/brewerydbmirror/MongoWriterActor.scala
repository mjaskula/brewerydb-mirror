package org.jaskula.brewerydbmirror

import akka.actor.Actor
import akka.actor.ActorRef
import com.mongodb.casbah.MongoClient
import play.api.Configuration
import play.api.libs.json.Json
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue

//TODO: extract interface?
class MongoWriterActor(config: Configuration) extends Actor {

  val mongodb =  MongoClient()(config.getString("mongodb.default.db").getOrElse("test"))
  
  def receive = {
    case ("style", styleJson: JsValue) =>  saveStyle(styleJson)
    case unsupportedMsg =>
      play.Logger.info("Received unsupported message '%s' in writer actor %s".format(unsupportedMsg, self.path.name))
  }
  
  private def saveStyle(styleJson: play.api.libs.json.JsValue) = {
    play.Logger.info("Received message 'style' in writer actor %s".format(self.path.name))
    // TODO: should style categories be their own collection?
    upsertById("styles", com.mongodb.util.JSON.parse(Json.stringify(styleJson)).asInstanceOf[DBObject])
  }
  
  private def upsertById(collName: String, data: DBObject): Int = {
    play.Logger.info("Saving to %s: %s".format(collName, data("name")))
    mongodb(collName).update(MongoDBObject("id" -> data("id")), data, upsert = true).getN()
  }
}