package org.jaskula.brewerydbmirror

import com.google.inject.Singleton
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
import com.google.inject.Inject
import org.jaskula.brewerydbclient.BreweryDbClient
import play.api.Configuration

@Singleton
class ActorPool @Inject()(config: Configuration,
                          breweryDbClient: BreweryDbClient) {

  val writerRouter = Akka.system.actorOf(Props(new MongoWriterActor(config)).
          withRouter(SmallestMailboxRouter(5)), "writerRouter")

  val readerRouter = Akka.system.actorOf(Props(new BreweryDbReaderActor(breweryDbClient, writerRouter)).
          withRouter(SmallestMailboxRouter(5)), "readerRouter")
          
  def reader = readerRouter 
}