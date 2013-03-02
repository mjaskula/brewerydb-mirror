package org.jaskula.brewerydbclient

import play.api.libs.ws.WS
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits._

class BreweryDbClient(apiKey: String) {

  def beersForStyle(styleId: String) = {
    WS.url("http://api.brewerydb.com/v2/beers")
      .withQueryString("key" -> apiKey,
                       "styleId" -> styleId).get().map { response =>
      response.json \ "data"
    }
    
  }
  
  
}

