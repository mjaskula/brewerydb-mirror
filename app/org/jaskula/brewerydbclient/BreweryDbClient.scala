package org.jaskula.brewerydbclient

import play.api.libs.ws.WS


class BreweryDbClient(apiKey: String) {

  def beersForStyle(styleId: String) = {
    WS.url("http://api.brewerydb.com/v2/beers")
      .withQueryString("key" -> apiKey,
                       "styleId" -> styleId).get()
    
  }
  
  
}

