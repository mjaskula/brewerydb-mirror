package org.jaskula.brewerydbmirror

import play.api._
import play.api.mvc._


object Global extends WithFilters(AccessLog)

object AccessLog extends Filter {
  override def apply(next: RequestHeader => Result)(request: RequestHeader): Result = {
    val result = next(request)
    play.Logger.info(request + "\n\t => " + result)
    result
  }
}