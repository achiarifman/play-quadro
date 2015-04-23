package models.dao

import org.bson.types.ObjectId

/**
 * Created by barcelona on 2/13/15.
 */
trait BaseDao {


  def getNewObjectIdAsString() = (new ObjectId()).toString

  def getNewObjectId() = new ObjectId()
}
