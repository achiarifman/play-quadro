package models.actors

import java.util.Calendar

import akka.actor.Actor.Receive
import models.dao.ProgrammeDao
import models.message.{TriggerRecordMessage, ScheduleRecordingsMessage}
import play.api.Logger
import scala.concurrent.duration._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import akka.actor._
import play.api.libs.concurrent.Akka
import models.QuadroPersistanceContext._
/**
 * Created by barcelona on 2/21/15.
 */
class RecordingSchedulerActor extends Actor{

  val HOURS = 60000 * 60 * 4 // four hours
  val SAFE_TIME = 1000 * 60 * 5 // start 5 minutes earlier
  val recordActor = context.system.actorOf(Props[RecordActor])
  val calendar = Calendar.getInstance()


  override def receive: Receive = {
    case ScheduleRecordingsMessage => schedule
  }

  def schedule = {
    Logger.info("Going to schedule record jobs for next " + HOURS / 1000 / 60  + " minutes")
    val current = System.currentTimeMillis()
    transactional{
    val navigator = ProgrammeDao.getProgrammesStartFrom(current + GMT_DIFF + HOURS,false)

    while(navigator.hasNext) {
      val programmes = navigator.next
      programmes.foreach(p => {
        if(p.startTime > System.currentTimeMillis())   {
          val scheduleTime = p.startTime - System.currentTimeMillis() - SAFE_TIME
          if(scheduleTime > 0 && !p.isScheduled) {
            val message = TriggerRecordMessage(p.id, p.url, (p.endTime - p.startTime + SAFE_TIME) / 1000, p.title, p.channelEpgId)
            Logger.info(Calendar.getInstance().getTime.toString +  " -> Scheduling " + p.id + " to " + (scheduleTime / 1000 / 60) + " minutes from now, programme " + message.name)
            ProgrammeDao.markProgrammeAsScheduled(p.id)
            context.system.scheduler.scheduleOnce(scheduleTime millisecond, recordActor, message)
          }
      }})
    }
    }
  }

}
