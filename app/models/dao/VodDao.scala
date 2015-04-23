package models.dao

import models.QuadroPersistanceContext._
import models.entity.Vod
import org.bson.types.ObjectId

/**
 * Created by barcelona on 2/11/15.
 */
object VodDao extends BaseDao{

  def createNewVod(name : String) = {

    transactional{
      val vod = Vod(getNewObjectIdAsString(),name)
    }
  }
}
