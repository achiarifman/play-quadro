package controllers

import controllers.Application._
import models.actors.Actors
import models.dao.{ProgrammeDao, ChannelDao}
import models.entity.{User, PriceHolder, Channel, CallbackMedia}
import models.json.ChannelJson
import models.message.PullEpgMessage
import play.api.data.Form
import play.api.data.Form._
import play.api.data.Forms._
import play.api.mvc._
import views.html

/**
 * Created by barcelona on 2/13/15.
 */
object Admin extends Controller with Secured{

  def epgPull = Action {
    Actors.epgActor ! PullEpgMessage
    Ok("Pulling EPG")
  }

  def transcoderCallback = Action { implicit request =>

    import spray.json._
    import models.entity.CallbackMediaSerializer._
    println(request.body.asJson.get.toString())
    val callbackMedia = request.body.asJson.get.toString().parseJson.convertTo[CallbackMedia]
    val programe =  ProgrammeDao.getProgrammeById(callbackMedia.programmeId)
    if(programe.isDefined){
      val channelList = ChannelDao.getChannelByEpgId(programe.get.channelEpgId)
      val channelOption = channelList.headOption
      if(channelOption.isDefined)
      ProgrammeDao.updateProgrammeUrl(callbackMedia.programmeId,callbackMedia.mediaUrl,PriceHolder(channelOption.get.goalPrice,
        channelOption.get.minPrice))
    }
    Ok
  }

  def createChannelForm = withAuth { username => implicit request =>
    if(checkIfAdmin(username)) {
      Ok(views.html.admin.createChannel(channelForm))
    } else {
      Unauthorized
    }
  }

  def createChannelSubmit =  withAuth { username => implicit request =>

    if(checkIfAdmin(username)) {
      channelForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.admin.channels(formWithErrors.toString, ChannelDao.getAllChannels)),
        channel => {
          ChannelDao.saveNewChannel(channel.copy(ads = channel.ads.filter(!_.isEmpty)))
          getChannels("Channel Added")
        }
      )
    } else {
      Unauthorized
    }

  }

  def editChannel(id : String) =  withAuth { username => implicit request =>
    if(checkIfAdmin(username)){
    val channel = ChannelDao.getChannelById(id)
    if(channel.isDefined){
      val channelJson = ChannelJson(channel.get.name,channel.get.number,channel.get.epgId,channel.get.recordingEnable,
      channel.get.url,channel.get.transcoderUrl,channel.get.ads,channel.get.goalPrice,channel.get.minPrice)
      Ok(views.html.admin.editChannel(channelForm.fill(channelJson), id,"Edit Channel"))
    }else {
      BadRequest(html.admin.editChannel(channelForm,"","User is invalid please signup again"))
    }
    }else {
      Unauthorized
    }

  }

  def editChannelSubmit(id : String) =  withAuth { username => implicit request =>
    if(checkIfAdmin(username)) {
      channelForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.admin.channels(formWithErrors.toString, ChannelDao.getAllChannels)),
        channel => {
          ChannelDao.updateChannel(id, channel.copy(ads = channel.ads.filter(!_.isEmpty)))
          getChannels("Channel Edited")
        }
      )
    } else {
      Unauthorized
    }
  }


  val channelForm : Form[ChannelJson] = Form {

    mapping(
      "name" ->  text,
      "number" -> number,
      "epgId" -> text,
      "recordingEnable" -> boolean,
      "url" -> text,
      "transcoderUrl" -> text,
      "ads" -> list(text),
      "goalPrice" -> bigDecimal,
      "minPrice" -> bigDecimal
    )(ChannelJson.apply)(ChannelJson.unapply)
  }

  def allChannels = Action{

    getChannels("All Channels")
  }

  private def getChannels(message : String) = Ok(views.html.admin.channels(message,ChannelDao.getAllChannels))

  def checkIfAdmin(username : String) = {
    username match {

      case "admin" => true
      case _ => false//BadRequest(html.signup(signUpForm,"User is invalid please signup again"))
    }
  }

}
