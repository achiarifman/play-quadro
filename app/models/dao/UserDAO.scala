package models.dao

import models.QuadroPersistanceContext._
import models.entity.User

/**
 * Created by barcelona on 2/21/15.
 */
object UserDAO extends BaseDao{

  def findOneByUsername(username: String) = {
    transactional{
      val user = select[User] where(u => u.username :== username)
      if(!user.isEmpty) Option(user.head) else None

    }
  }

  def findById(id : String) = {
    transactional{
      byId[User](id)
    }
  }

  def createUser(userName : String, password : String) = {
    transactional{
      val newUser = new User(getNewObjectIdAsString(),userName,password)
      newUser
    }
  }

  def addProgrammeToUser(id : String, programmeId : String) = {
    transactional{
      val user = findById(id)
      if(user.isDefined){
        val uMap = new MutableEntityMap[User]()
        val updatedList = user.get.programmesList :+ programmeId
        uMap.put(_.programmesList)(updatedList)
        uMap.tryUpdate(id)
      }else {
        user
      }

    }
  }

  def removeProgrammeToUser(id : String, programmeId : String) = {
    transactional{
      val user = findById(id)
      if(user.isDefined){
        val uMap = new MutableEntityMap[User]()
        val updatedList = user.get.programmesList.toBuffer -=  programmeId
        uMap.put(_.programmesList)(updatedList.toList)
        uMap.tryUpdate(id)
      }else {
        user
      }


    }
  }

  def getAllUsers = {
    transactional{
      val navigator = paginatedQuery {
        (entity: User) =>
          where((entity.programmesList.size :> 0)) select (entity) orderBy (entity.username)
      }.navigator(50)
      navigator
    }
  }

  def addToTotalFee(id : String, fee : BigDecimal) : Unit = {
    transactional {
      val user = findById(id)
      if (user.isDefined) {
        val uMap = new MutableEntityMap[User]()
        val totalFee = user.get.totalFee
        uMap.put(_.totalFee)(totalFee + fee)
        uMap.tryUpdate(id)
      }
    }
  }

}
