package org.jaskula.brewerydbclient

import play.api.libs.ws.WS
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import scala.concurrent.Future

class BreweryDbClient(apiKey: String) {

  def beersJsonForStyle(styleId: String): Future[Seq[JsObject]] = {
    WS.url("http://api.brewerydb.com/v2/beers")
      .withQueryString("key" -> apiKey,
                       "styleId" -> styleId).get().map { response =>
      (response.json \ "data").as[Seq[JsObject]]
    }
    
  }
  
  
}

