package models.actors

import java.io.File

import akka.actor.Actor
import models.dao.{ProgrammeDao, ChannelDao}
import models.message.PullEpgMessage
import models.service.XmltvToProgrammeService
import models.utils.PropsConsts
import play.api.Logger
import play.api.Play._


/**
 * Created by barcelona on 2/20/15.
 */
class EpgActor extends Actor{

  val DROPBOX_PATH = current.configuration.getString(PropsConsts.DROPBOX_PATH).getOrElse("/Users/barcelona/Dropbox/Public/")
  val xmltvFile = DROPBOX_PATH + "epg" + File.separator + "EPGdata.xml"

  override def receive: Receive = {

    case PullEpgMessage => pullEpg()
  }

  def pullEpg() = {


    val allChannels = ChannelDao.getAllChannels

    Logger.info("Deleting future epg before fetching new once")
    ProgrammeDao.deleteFutureProgrammes(System.currentTimeMillis() + GMT_DIFF + FOUR_HOURS)
    Logger.info("finish to delete future epg before fetching")

    Logger.info("Got epg pull message, pulling the Epg")
    XmltvToProgrammeService.mapToProgramme(xmltvFile)
    Logger.info("Finish to pull the Epg")

  }
}
