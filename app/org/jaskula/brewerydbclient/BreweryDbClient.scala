package org.jaskula.brewerydbclient

import play.api.libs.ws.WS
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import scala.concurrent.Future

class BreweryDbClient(apiKey: String) {

  val apiUrlRoot = "http://api.brewerydb.com/v2/"
  
  def stylesJson(): Future[Seq[JsObject]] = breweryDbCall("styles")

  def beersJsonForStyle(styleId: String): Future[Seq[JsObject]] =
    breweryDbCall("beers", "styleId" -> styleId, "withBreweries" -> "Y")
  
  // TODO: add error handling
  private def breweryDbCall(endpoint: String, parameters: (String, String)*): Future[Seq[JsObject]] = {
    WS.url(apiUrlRoot + endpoint).withQueryString("key" -> apiKey)
                                 .withQueryString(parameters: _*).get().map { response =>
        (response.json \ "data").as[Seq[JsObject]]
    }
  }
}

