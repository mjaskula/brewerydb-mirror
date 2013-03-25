package org.jaskula.brewerydbmirror

import play.api._
import play.api.mvc._
import play.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import javax.inject._
import play.api.libs.json.Json

@Singleton
class Application @Inject()(mirror: Mirror,
                            mirrorStatus: MirrorStatus,
                            generator: JsonGenerator) extends Controller {
  
  //  Display
  
  def status = Action {
    AsyncResult {
      mirrorStatus.status.map { status => 
        Ok(status.toString)
      }
    }
  }
  
  def beers = Action {
    Ok("beers")
  }

  // Mirroring
  
  def loadAll() = Action {
    mirror.loadAll()
    Redirect(routes.Application.status)
  }
  
  def loadAllStyles() = Action {
    mirror.loadAllStyles()
    Redirect(routes.Application.status)
  }
  
  def loadBeersForStyle(styleId: String) = Action {
    mirror.loadBeersForStyle(styleId)
    Redirect(routes.Application.status)
  }
  
  
  // Generate
  
  def generate() = Action {
    AsyncResult {
      generator.generateBeers.map { beers =>
        Ok(beers)
      }
    }
    
  }
}