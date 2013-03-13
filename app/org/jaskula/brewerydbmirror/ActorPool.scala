package org.jaskula.brewerydbmirror

import com.google.inject.Singleton
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
import com.google.inject.Inject
import org.jaskula.brewerydbclient.BreweryDbClient
import play.api.Configuration
import org.jaskula.brewerydbmirror.MongoService

@Singleton
class ActorPool @Inject()(breweryDbClient: BreweryDbClient,
                          mongoService: MongoService) {

  val writerRouter = Akka.system.actorOf(Props(new MongoWriterActor(mongoService)).
          withRouter(SmallestMailboxRouter(5)), "writerRouter")

  val readerRouter = Akka.system.actorOf(Props(new BreweryDbReaderActor(breweryDbClient, writerRouter)).
          withRouter(SmallestMailboxRouter(5)), "readerRouter")
          
  def reader = readerRouter 
}

object MessageType extends Enumeration {
  type MessageType = Value
  val ReadStyles, ReadBeers,
      WriteStyle, WriteBeer
      = Value
}