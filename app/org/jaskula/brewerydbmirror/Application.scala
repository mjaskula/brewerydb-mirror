package org.jaskula.brewerydbmirror

import play.api._
import play.api.mvc._
import play.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import com.google.inject._

@Singleton
class Application @Inject()(mirror: Mirror,
                            mirrorStatus: MirrorStatus) extends Controller {
  
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

  def loadAll() = Action {
    mirror.loadAll()
    Redirect(routes.Application.status)
  }
  
  def loadAllStyles() = Action {
    mirror.loadAllStyles()
    Redirect(routes.Application.status)
  }
  
  def loadAllBeers() = Action {
    mirror.loadAllBeers()
    Redirect(routes.Application.status)
  }
  
}