package org.jaskula.brewerydbmirror

import play.api._
import play.api.Play.current
import play.api.mvc._
import com.google.inject.Guice


object Global extends WithFilters(AccessLog) {
   private lazy val injector = {
    Play.isProd match {
      case true => Guice.createInjector(new ProdModule)
      case false => Guice.createInjector(new DevModule)
    }
  }
 
  override def getControllerInstance[A](clazz: Class[A]) = {
    injector.getInstance(clazz)
  }
}

object AccessLog extends Filter {
  override def apply(next: RequestHeader => Result)(request: RequestHeader): Result = {
    val result = next(request)
    play.Logger.info(request + "\n\t => " + result)
    result
  }
}