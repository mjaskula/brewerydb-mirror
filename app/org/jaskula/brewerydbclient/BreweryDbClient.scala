package org.jaskula.brewerydbclient

import play.api.libs.ws.WS
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import scala.concurrent.Future
import com.google.inject._
import play.api.Configuration
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json._
import play.api.libs.functional.syntax._

@Singleton
class BreweryDbClient @Inject()(config: Configuration, stats: StatsStorageProvider) {

  val apiKey = config.getString("brewerydb.apikey").getOrElse("specify apikey in 'conf/brewerydb.apikey.conf'")
  val apiUrlRoot = "http://api.brewerydb.com/v2/"
  
  val responseReads: Reads[BreweryResponse] = (
        (JsPath \ "data").read[Seq[JsObject]] and
        (JsPath \ "currentPage").readNullable[Int] and
        (JsPath \ "numberOfPages").readNullable[Int] and
        (JsPath \ "totalResults").readNullable[Int]
        
  )(BreweryResponse)
    
  def stylesJson(): Future[BreweryResponse] = breweryDbCall("styles", 0)

  def beersJsonForStyle(styleId: String, page: Int = 0): Future[BreweryResponse] =
    breweryDbCall("beers", page, "styleId" -> styleId, "withBreweries" -> "Y")
  
  // TODO: add error handling
  private def breweryDbCall(endpoint: String, page: Int, parameters: (String, String)*): Future[BreweryResponse] = {
    WS.url(apiUrlRoot + endpoint).withQueryString("key" -> apiKey)
                                 .withQueryString("p" -> page.toString)
                                 .withQueryString(parameters: _*).get().map { response =>
        stats.countApiCall()
        play.Logger.info("Gathering page %d of %s".format(page, endpoint))
        responseReads.reads(response.json).fold(
          valid = { response => response },
          invalid = { e => throw BreweryDbException(e.toString) }
        )
    }
  }
  
  case class BreweryResponse(data: Seq[JsObject],
                             currentPage: Option[Int],
                             numberOfPages: Option[Int],
                             totalResults: Option[Int]) {
    
  }
  
  case class BreweryDbException(message: String = null, cause: Throwable = null) extends Exception
}