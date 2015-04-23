package models.actors

import akka.actor.Actor
import models.dao.ChannelDao
import models.entity.RecordInfo
import models.message.TriggerRecordMessage
import models.utils.PropsConsts
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import scala.concurrent.{Await, Future}
import play.api.Logger
import dispatch._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
/**
 * Created by barcelona on 2/20/15.
 */
class RecordActor extends Actor{

  val RECORD_PATH = "/eagle/transcode/channel"
  val CALLBACK_IP = current.configuration.getString(PropsConsts.HOST_IP).getOrElse("http://localhost")
  val CALLBACK_URL = CALLBACK_IP + "/admin/callback"

  override def receive: Receive = {
    case message : TriggerRecordMessage => sendRecordTrigger(message)
  }

  def sendRecordTrigger(message : TriggerRecordMessage) = {

    import spray.json._
    import models.entity.RecordInfoSerializer._

    Logger.info("Got record message for channel " + message.channelEpgId + " of programme " + message.name)
    val channelsResult = ChannelDao.getChannelByEpgId(message.channelEpgId)
    if(!channelsResult.isEmpty && channelsResult.head.recordingEnable){

      val channel = channelsResult.head
      val recordInfo = RecordInfo(channel.url,message.duration,channel.name,channel.ads,CALLBACK_URL,message.id)
      val data = recordInfo.toJson
      val req = url(channel.transcoderUrl + ":8080" + RECORD_PATH).setContentType("application/json", "UTF-8").
        addHeader("Accept","application/json; charset=UTF-8") << data.toString()
      val response = Http(req.POST OK as.Response(r => r))
      Await.result(response, 15 seconds)
      }
    else {
      Logger.info("Recording of channel " + message.channelEpgId + " is disabled")
    }
  }
}
