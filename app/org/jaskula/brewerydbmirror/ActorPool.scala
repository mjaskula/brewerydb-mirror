package org.jaskula.brewerydbmirror

import javax.inject.Singleton
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
import javax.inject.Inject
import org.jaskula.brewerydbclient.BreweryDbClient
import play.api.Configuration

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
  val ReadAll, ReadStyles, ReadBeersForStyle,
      WriteStyle, WriteBeer
      = Value
}