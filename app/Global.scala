import models.actors.Actors
import models.dao.ProgrammeDao
import models.message._
import models.utils.{PropsConsts, QuartzScheduler}
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.{Application, GlobalSettings, Logger}
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by barcelona on 2/13/15.
 */
object Global extends GlobalSettings{


  override def onStart(app: Application): Unit = {
    Logger.info("Application has started - quadro")
    QuartzScheduler.start()
    scheduleEpgPuller(app)
    scheduleRecordingScheduler(app)
    scheduleRemoveVods(app)
    scheduleCalculateFees(app)
  }

  override def onStop(app: Application) {
    QuartzScheduler.stop()
  }

  def scheduleEpgPuller(app: Application) = {

    ProgrammeDao.deleteFutureProgrammes(System.currentTimeMillis())
    Logger.info("Scheduling the EPG puller")
    val epgSchedule = current.configuration.getString(PropsConsts.EPG_SCHEDULE)
    QuartzScheduler schedule("epg pull", pullEpg) at epgSchedule.getOrElse(PropsConsts.EPG_SCHEDULE_DEFAULT)
    pullEpg
  }

  def scheduleRecordingScheduler(app : Application) = {
    Logger.info("Scheduling the Recording scheduler")
    Akka.system.scheduler.schedule(1 minutes,3 hours,Actors.recordSchedulerActor,ScheduleRecordingsMessage)
  }

  def scheduleRemoveVods(app : Application) = {
    Logger.info("Scheduling remove vods job")
    val dailySchedule = current.configuration.getString(PropsConsts.DAILY_SCHEDULE)
    QuartzScheduler schedule("removeNotSavedVods", removeNotSavedVods) at dailySchedule.getOrElse(PropsConsts.DAILY_SCHEDULE_DEFAULT)
    QuartzScheduler schedule("removeForceVods", removeForceVods) at dailySchedule.getOrElse(PropsConsts.DAILY_SCHEDULE_DEFAULT)
    removeNotSavedVods
  }

  def scheduleCalculateFees(app : Application) = {
    Logger.info("Scheduling calculate fees job")
    val dailySchedule = current.configuration.getString(PropsConsts.DAILY_SCHEDULE)
    QuartzScheduler schedule("calculateTotalFees", calculateTotalFees) at dailySchedule.getOrElse(PropsConsts.DAILY_SCHEDULE_DEFAULT)
  }

  def removeNotSavedVods = {
    Logger.info("Sending RemoveUnSavedVods message to removeVodsActor Actor")
    val days = current.configuration.getInt(PropsConsts.REMOVE_NOT_SAVED)
    Actors.removeVodsActor ! RemoveUnSavedVods(days.getOrElse(PropsConsts.REMOVE_NOT_SAVED_DEFAULT))
  }

  def removeForceVods = {
    Logger.info("Sending RemoveForceVods message to removeVodsActor Actor")
    val days = current.configuration.getInt(PropsConsts.REMOVE_FORCE)
    Actors.removeVodsActor ! RemoveForceVods(days.getOrElse(PropsConsts.REMOVE_FORCE_DEFAULT))
  }

  def pullEpg = {
    Logger.info("Sending pull epg message to Epg Actor")
    Actors.epgActor ! PullEpgMessage
  }

  def calculateTotalFees = {
    Logger.info("Sending calculate fees message to Calculate Actor")
    Actors.calculateFeesActor ! CalculateFeesMessage()
  }
}
