package models.actors

import akka.actor.Actor
import akka.actor.Actor.Receive
import models.dao.{UserDAO, ProgrammeDao}
import models.message.CalculateFeesMessage
import models.QuadroPersistanceContext._
/**
 * Created by barcelona on 4/11/15.
 */
class CalculateFeesActor extends Actor{

  override def receive: Receive = {
    case message : CalculateFeesMessage => calculate
  }

  def calculate = {

    transactional{
      val usersNavigator = UserDAO.getAllUsers
      while(usersNavigator.hasNext){
        val usersPage = usersNavigator.next
        usersPage.foreach(user => {
          val programmesFees = user.programmesList.map(getProgrammePrice(_))
          val totalFee = programmesFees.sum
          UserDAO.addToTotalFee(user.id,totalFee)
        })
      }
    }
  }

  def getProgrammePrice(id : String) : BigDecimal = {
    val programme = ProgrammeDao.getProgrammeById(id)
    val price = programme match{
      case Some(p) => p.price
      case _ => BigDecimal(0)
    }
    price
  }
}
