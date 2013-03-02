package org.jaskula.brewerydbmirror

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import org.jaskula.brewerydbclient.BreweryDbClient
import play.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

object Application extends Controller {
  
  val config = play.api.Play.configuration
  val breweryDbClient = new BreweryDbClient(config.getString("brewerydb.apikey").getOrElse("specify apikey in 'conf/brewerydb.apikey.conf'"))
  
  def index = Action {
    Ok("index")
  }
  
  def beers = Action {
	Ok("beers")
  }

  def populate() = Action {
  AsyncResult {
    breweryDbClient.beersForStyle("25").map { beers =>
      Ok(beers)
    }
  }  
}
}