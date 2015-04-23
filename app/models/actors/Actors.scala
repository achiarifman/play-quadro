package models.actors

/**
 * Created by barcelona on 2/20/15.
 */
import akka.actor._
import play.api._
import play.api.libs.concurrent.Akka


class Actors(implicit app: Application) extends Plugin {

  lazy val epgActor = Akka.system.actorOf(Props[EpgActor], "epg-actor")
  lazy val recordSchedulerActor = Akka.system.actorOf(Props[RecordingSchedulerActor], "recordScheduler-actor")
  lazy val removeVodsActor = Akka.system.actorOf(Props[RemoveVodsActor], "removeVods-actor")
  lazy val calculateFeesActor = Akka.system.actorOf(Props[CalculateFeesActor], "calculateFees-actor")
}

object Actors {


  def epgActor: ActorRef = Play.current.plugin[Actors]
    .getOrElse(throw new RuntimeException("Actors plugin not loaded"))
    .epgActor

  def recordSchedulerActor: ActorRef = Play.current.plugin[Actors]
    .getOrElse(throw new RuntimeException("Actors plugin not loaded"))
    .recordSchedulerActor

  def removeVodsActor: ActorRef = Play.current.plugin[Actors]
    .getOrElse(throw new RuntimeException("Actors plugin not loaded"))
    .removeVodsActor

  def calculateFeesActor: ActorRef = Play.current.plugin[Actors]
    .getOrElse(throw new RuntimeException("Actors plugin not loaded"))
    .calculateFeesActor


}