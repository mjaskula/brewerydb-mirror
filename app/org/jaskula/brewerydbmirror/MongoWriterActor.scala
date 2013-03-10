package org.jaskula.brewerydbmirror

import akka.actor.Actor
import akka.actor.ActorRef

//TODO: extract interface?
class MongoWriterActor extends Actor {

  def receive = {
    case (msg, respondTo:ActorRef) =>
      play.Logger.info("Received message '%s' in writer actor %s".format(msg, self.path.name))
      respondTo ! 3
  }
}