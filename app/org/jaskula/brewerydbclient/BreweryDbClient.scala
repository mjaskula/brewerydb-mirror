package org.jaskula.brewerydbclient

import play.api.libs.ws.WS
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import scala.concurrent.Future
import com.google.inject._
import play.api.Configuration

@Singleton
class BreweryDbClient @Inject()(config: Configuration, stats: StatsStorageProvider) {

  val apiKey = config.getString("brewerydb.apikey").getOrElse("specify apikey in 'conf/brewerydb.apikey.conf'")
  val apiUrlRoot = "http://api.brewerydb.com/v2/"
  
  def stylesJson(): Future[Seq[JsObject]] = breweryDbCall("styles")

  def beersJsonForStyle(styleId: String): Future[Seq[JsObject]] =
    breweryDbCall("beers", "styleId" -> styleId, "withBreweries" -> "Y")
  
  // TODO: add error handling
  // TODO: add page support
  private def breweryDbCall(endpoint: String, parameters: (String, String)*): Future[Seq[JsObject]] = {
    WS.url(apiUrlRoot + endpoint).withQueryString("key" -> apiKey)
                                 .withQueryString(parameters: _*).get().map { response =>
        stats.countApiCall()
        play.Logger.info("Gathering %s data %s".format(endpoint, parameters))
        (response.json \ "data").as[Seq[JsObject]]
    }
  }
}

