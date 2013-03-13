package org.jaskula.brewerydbmirror

import play.api._
import play.api.mvc._
import play.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import com.google.inject._

@Singleton
class Application @Inject()(mirror: Mirror) extends Controller {
  
  def index = Action {
    Ok("index")
  }
  
  def beers = Action {
	Ok("beers")
  }

  def loadAll() = Action {
    mirror.loadAllStyles()
    mirror.loadAllBeers()
    Ok("Load all started")
  }
  
  def loadAllStyles() = Action {
    mirror.loadAllStyles()
    Ok("Load all styles started")
  }
  
  def loadAllBeers() = Action {
    mirror.loadAllBeers()
    Ok("Load all beers started")
  }
  
}