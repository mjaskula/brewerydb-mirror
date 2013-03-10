package org.jaskula.brewerydbmirror
 
import com.tzavellas.sse.guice.ScalaModule
import play.api.Configuration
import play.api.Play.current
import org.jaskula.brewerydbclient.MongoStatsStorageProvider
import org.jaskula.brewerydbclient.StatsStorageProvider
 


class CommonModule extends ScalaModule {
  def configure() {
    bind[Configuration].toInstance(play.api.Play.configuration)
    bind[StatsStorageProvider].to[MongoStatsStorageProvider]
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