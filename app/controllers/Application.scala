package controllers


import models.dao.{ProgrammeDao, UserDAO, ChannelDao, VodDao}
import models.service.XmltvToProgrammeService
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import views.html
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit.DAYS

object Application extends Controller with Secured{

  val ADD = "add"
  val REMOVE = "remove"
  val PLAY = "play"

  def index = withAuth { username => implicit request =>
    Ok(html.index(username))
  }

  def vod(programmeId : String,operation : String) = withAuth { username => implicit request =>
    val user = UserDAO.findOneByUsername(username)
    user match{
      case Some(user) => {
        operation match {

          case ADD|REMOVE => {
            operation match {
              case ADD => {
                UserDAO.addProgrammeToUser(user.id,programmeId)
                ProgrammeDao.increaseSavedCounter(programmeId)
              }
              case REMOVE => {
                UserDAO.removeProgrammeToUser(user.id,programmeId)
                ProgrammeDao.decreaseSavedCounter(programmeId)
              }
            }

            val progOption = ProgrammeDao.getProgrammeById(programmeId)
            if(progOption.isDefined){
               val newPrice = calculatePrice(progOption.get.savedCounter,progOption.get.goalPrice,progOption.get.price)
              ProgrammeDao.updatePrice(programmeId, newPrice)
            }
          }

          case _ =>
        }
        val programmesOption = user.programmesList.map(programme => ProgrammeDao.getProgrammeById(programme))
        val programmes = programmesOption.filter(_.isDefined).map(_.get)
        val requestedProgramme = operation match {
          case _ =>  {
            val requestedOption = programmes.find(p => p.id.equals(programmeId))
            if (requestedOption.isDefined) requestedOption else programmes.headOption
          }
        }
        Ok(views.html.vods(username,requestedProgramme,programmes))
      }
      case _ => BadRequest(html.signup(signUpForm,"User is invalid please signup again"))
    }
  }

  def live(channelNumber: String) = withAuth { username => implicit request =>
    val channels = ChannelDao.getAllChannels
    val requestedChannel = channels.find(p => p.number.toString.equals(channelNumber)).getOrElse(channels.head)
    val duration = new FiniteDuration(3,DAYS)
    val programmeList = ProgrammeDao.getProgramsFromByEpgId(requestedChannel.epgId,System.currentTimeMillis() - duration.toMillis)
    Ok(views.html.live("Live!",channels,requestedChannel,programmeList))
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def signup = Action { implicit request =>
    Ok(html.signup(signUpForm,""))
  }

  def createUser = Action { implicit request =>
    signUpForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.signup(formWithErrors,"Form validation error or user already exists")),
      user => {
        UserDAO.createUser(user._1,user._2)
        Redirect(routes.Application.index).withSession(Security.username -> user._1)
      }
    )
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession(Security.username -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }

  def getUserDetails = withAuth { username => implicit request =>
    val userOption = UserDAO.findOneByUsername(username)
    if(userOption.isDefined){
      Ok(views.html.account(username,userOption.get))
    }else {
      BadRequest(html.signup(signUpForm,"User is invalid please signup again"))
    }

  }

  def check(username: String, password: String) = {
    val foundUser = UserDAO.findOneByUsername(username)
    (foundUser.isDefined && username == foundUser.get.username && password == foundUser.get.password)
  }

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => check(email, password)
    })
  )

  val signUpForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => UserDAO.findOneByUsername(email).isEmpty
    })
  )

  private def calculatePrice(users : Int, goalPrice : BigDecimal, minPrice : BigDecimal) = {

    val pricePerUser = goalPrice / users
    if(pricePerUser < minPrice) pricePerUser else minPrice
  }



}