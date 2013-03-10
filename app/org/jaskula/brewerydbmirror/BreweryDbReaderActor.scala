package org.jaskula.brewerydbmirror

import akka.actor.Actor
import akka.actor.ActorRef

class BreweryDbReaderActor(writer: ActorRef) extends Actor {
  
  def receive = {
    case msg =>
      play.Logger.info("Received message '%s' in reader actor %s, responds to %s".format(msg, self.path.name, sender))
      writer ! ("message", sender)
  }
}
