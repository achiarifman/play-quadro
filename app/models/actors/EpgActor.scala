package models.actors

import java.io.File

import akka.actor.Actor
import models.dao.ChannelDao
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

    //we need to pull the epg from the epg server
    //.....
    val allChannels = ChannelDao.getAllChannels
    Logger.info("Got epg pull message, pulling the Epg")
   /* allChannels.map(c => {

    })*/
    XmltvToProgrammeService.mapToProgramme(xmltvFile)
    Logger.info("Finish to pull the Epg")

  }
}
