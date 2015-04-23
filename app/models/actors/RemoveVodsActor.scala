package models.actors

import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive
import models.dao.ProgrammeDao
import models.entity.Programme
import models.message.{DeleteVodMessage, RemoveUnSavedVods, RemoveForceVods}
import play.api.libs.concurrent.Akka
import models.QuadroPersistanceContext._
/**
 * Created by barcelona on 4/18/15.
 */
class RemoveVodsActor extends Actor{

  val dayInMilli = 1000 * 60 * 60 * 24
  val deleteVodActor = context.system.actorOf(Props[DeleteVodActor], "deleteVod-actor")

  override def receive: Receive = {
    case message : RemoveForceVods => executeRemoveForceVods(message.days)

    case message : RemoveUnSavedVods => executeRemoveForceVods(message.days)
  }

  def executeRemoveForceVods(days : Int) = {

    transactional{
      val programmesNavigator = ProgrammeDao.getProgrammesEarlierThan(System.currentTimeMillis() - dayInMilli * days)
      while(programmesNavigator.hasNext){
        deleteVods(programmesNavigator.next)
      }
    }

  }

  def executeRemoveUnSavedVods(days : Int) = {
    transactional {
      val programmesNavigator = ProgrammeDao.getEarlierThanNoSaved(System.currentTimeMillis() - dayInMilli * days)
      while (programmesNavigator.hasNext) {
        deleteVods(programmesNavigator.next)
      }
    }
  }

  def deleteVods(programmes : List[Programme]) = {

    programmes.foreach(programme => {
      deleteVodActor ! DeleteVodMessage(programme.id)
    })
    ProgrammeDao.deleteList(programmes.map(_.id))
  }


}
