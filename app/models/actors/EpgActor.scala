package models.actors

import akka.actor.Actor
import models.dao.ChannelDao
import models.message.PullEpgMessage
import models.service.XmltvToProgrammeService
import play.api.Logger


/**
 * Created by barcelona on 2/20/15.
 */
class EpgActor extends Actor{

  val xmltvFile = "/Users/barcelona/Dropbox/Public/epg/EPGdata.xml"

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
