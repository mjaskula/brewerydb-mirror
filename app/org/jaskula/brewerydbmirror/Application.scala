package org.jaskula.brewerydbmirror

import play.api._
import play.api.mvc._
import play.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import com.google.inject._

@Singleton
class Application @Inject()(dataProcessor: DataProcessor) extends Controller {
  
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