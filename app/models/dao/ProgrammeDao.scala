package models.dao

import models.entity.{PriceHolder, Programme, ProgrammeElement}
import models.QuadroPersistanceContext._

/**
 * Created by barcelona on 2/13/15.
 */
object ProgrammeDao extends BaseDao{


  def saveNewProgrammes(programmes : List[(ProgrammeElement,PriceHolder)]) = {
    transactional{
      programmes.foreach(p => {
        val isExist = transactional {
          select[Programme] where (c => (c.startTime :== p._1.startTime) :&& (c.channelEpgId :== p._1.channelEpgId))
        }
          if(isExist.isEmpty){
          new Programme(getNewObjectIdAsString(),p._1.channelEpgId,p._1.title,p._1.startTime,p._1.endTime,p._1.url,p._2.goalPrice,p._2.price)}
        })
    }
  }

  def getProgrammesStartFrom(start : Long, scheduled : Boolean) = {
    val navigator = paginatedQuery {
      (entity: Programme) =>
        where((entity.startTime :<= start) :&& (entity.isScheduled :== scheduled)) select (entity) orderBy (entity.startTime)
    }.navigator(100)
    navigator
  }

  def getProgramsFromByEpgId(epgId : String, start : Long) = {
    transactional {
      val navigator = paginatedQuery {
        (entity: Programme) =>
          where((entity.startTime :>= start) :&& (entity.channelEpgId :== epgId)) select (entity) orderBy (entity.startTime)
      }.navigator(500)
      navigator.firstPage
    }
  }

  def updateProgrammeUrl(id : String, url : String, priceHolder: PriceHolder) = {
    transactional {
      val pMap = new MutableEntityMap[Programme]()
      pMap.put(_.recordedUrl)(url)
      pMap.put(_.isRecorded)(true)
      pMap.put(_.goalPrice)(priceHolder.goalPrice)
      pMap.put(_.goalPrice)(priceHolder.price)
      pMap.tryUpdate(id)
    }
  }

  def markProgrammeAsScheduled(id : String) = {
    transactional{
      val pMap = new MutableEntityMap[Programme]()
      pMap.put(_.isScheduled)(true)
      pMap.tryUpdate(id)
    }
  }

  def getProgrammeById(id : String) = {
    transactional{
      byId[Programme](id)
    }
  }

  def increaseSavedCounter(id : String) = {
    transactional{
      val programme = getProgrammeById(id)
      if(programme.isDefined){
        val pMap = new MutableEntityMap[Programme]()
        pMap.put(_.savedCounter)(programme.get.savedCounter + 1)
        pMap.tryUpdate(id)
      }
    }
  }

  def decreaseSavedCounter(id : String) = {
    transactional{
      val programme = getProgrammeById(id)
      if(programme.isDefined){
        val pMap = new MutableEntityMap[Programme]()
        pMap.put(_.savedCounter)(programme.get.savedCounter - 1)
        pMap.tryUpdate(id)
      }
    }
  }

  def updatePrice(id : String, price : BigDecimal) = {
    transactional{
      val pMap = new MutableEntityMap[Programme]()
      pMap.put(_.price)(price)
      pMap.tryUpdate(id)
    }
  }

  def getProgrammesEarlierThan(timestamp : Long) = {

    transactional{
      val navigator = paginatedQuery {
        (entity: Programme) =>
          where((entity.endTime :<= timestamp)) select (entity) orderBy (entity.startTime)
      }.navigator(100)
      navigator
    }
  }

  def getEarlierThanNoSaved(timestamp : Long) = {

    transactional{
      val navigator = paginatedQuery {
        (entity: Programme) =>
          where((entity.endTime :<= timestamp) :&& (entity.savedCounter :== 0)) select (entity) orderBy (entity.startTime)
      }.navigator(100)
      navigator

    }
  }

  def deleteList(ids : List[String]) = {
    transactional{
          delete{
      (entity: Programme) => where(entity.id in(ids))
          }
    }
  }

  def deleteFutureProgrammes(startFrom : Long) = {
    transactional{
      delete{
        (entity: Programme) => where(entity.startTime :> startFrom)
      }
    }
  }

}
