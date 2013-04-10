package org.jaskula.brewerydbmirror

import com.google.inject.Singleton
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._
import play.api.libs.json._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes._
import play.api.libs.json.Reads._

@Singleton
class JsonGenerator {

  // TODO: move to mongo service
  val db = ReactiveMongoPlugin.db
  lazy val collection = db("beers")

  val beerTransformer = (JsPath \ '_id).json.prune

  // TODO: is this overkill?
  def generateBeers(): Future[JsValue] = {
    val qb = QueryBuilder().query(Json.obj()).sort( "styleId" -> SortOrder.Ascending)
    
    // TODO: use Mongo Reader?
    collection.find[JsValue]( qb ).toList.map { persons =>
      persons.foldLeft(JsArray(List())) { (obj, person) => 
        person.transform(beerTransformer).fold(
          invalid = { errors => 
            play.Logger.error("Error validating json:" + errors.toString)
            obj
          },
          valid = { res => 
            obj ++ Json.arr(res)
          }
        )
        
      }
    }
  }
}