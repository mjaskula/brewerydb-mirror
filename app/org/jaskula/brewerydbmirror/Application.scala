package org.jaskula.brewerydbmirror

import play.api._
import play.api.mvc._
import play.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

object Application extends Controller {
  
  val dataProcessor = new DataProcessor(play.api.Play.configuration)
  
  def index = Action {
    Ok("index")
  }
  
  def beers = Action {
	Ok("beers")
  }

  def updateStyles() = Action {
    AsyncResult {
      dataProcessor.updateStyles().map { count =>
        Ok("Updated %d styles".format(count))
      }
    }
  }
  
  def populate() = Action {
    AsyncResult {
      dataProcessor.updateBeersForStyle("25").map { stats =>
        Ok("Updated: %s".format(stats.toString))
      }
    }
  }
  
}