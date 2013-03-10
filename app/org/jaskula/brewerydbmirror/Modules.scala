package org.jaskula.brewerydbmirror
 
import com.tzavellas.sse.guice.ScalaModule
import play.api.Configuration
import play.api.Play.current
 


class CommonModule extends ScalaModule {
  def configure() {
    bind[Configuration].toInstance(play.api.Play.configuration)
  }
}

class ProdModule extends CommonModule {
  override def configure() {
    super.configure
  }
}
 
class DevModule extends CommonModule {
  override def configure() {
    super.configure
  }
}