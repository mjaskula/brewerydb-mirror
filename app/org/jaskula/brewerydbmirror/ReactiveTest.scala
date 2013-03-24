package org.jaskula.brewerydbmirror

import play.api._
import play.api.mvc._

// Reactive Mongo imports
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._

// Reactive Mongo plugin
import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._

// Play Json imports
import play.api.libs.json._

import play.api.Play.current

object ReactiveTest extends Controller with MongoController {
  val db = ReactiveMongoPlugin.db
  lazy val collection = db("beers")

  // queries for a person by name
  def findByName(id: Int) = Action {
    Async {
      val qb = QueryBuilder().query(Json.obj( "styleId" -> id )).sort( "createDate" -> SortOrder.Descending)

      collection.find[JsValue]( qb ).toList.map { persons =>
        Ok(persons.foldLeft(JsArray(List()))( (obj, person) => obj ++ Json.arr(person) ))
      }
    }
  } 

}
