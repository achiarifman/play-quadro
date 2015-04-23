package models.dao

import models.QuadroPersistanceContext._
import models.entity.Channel
import models.json.ChannelJson

/**
 * Created by barcelona on 2/13/15.
 */
object ChannelDao extends BaseDao{


  def getAllChannels = {
    transactional{
      all[Channel]
    }
  }

  def getChannelByEpgId(epgId : String) = {
    transactional{
      select[Channel] where(c => c.epgId :== epgId)
    }
  }

  def saveNewChannel(channelJson : ChannelJson) = {
    transactional{
      val c = select[Channel] where(c => (c.epgId :== channelJson.epgId) :|| (c.number :== channelJson.number))
      if(c.isEmpty){
        val newChannel = new Channel(getNewObjectIdAsString(),channelJson.name,channelJson.number,channelJson.epgId,
          channelJson.recordingEnable, channelJson.url, channelJson.transcoderUrl,channelJson.ads,channelJson.goalPrice,
        channelJson.minPrice)
        newChannel
      }else{

      }

    }
  }

  def createChannel(channel : Channel) = {
    transactional{
      channel.copy()
    }
  }

  def getChannelById(id : String) = {
    transactional{
      byId[Channel](id)
    }
  }

  def updateChannel(id : String,channelJson : ChannelJson) = {
    transactional{
      val cMap = new MutableEntityMap[Channel]()
      cMap.put(_.name)(channelJson.name)
      cMap.put(_.number)(channelJson.number)
      cMap.put(_.epgId)(channelJson.epgId)
      cMap.put(_.ads)(channelJson.ads)
      cMap.put(_.recordingEnable)(channelJson.recordingEnable)
      cMap.put(_.transcoderUrl)(channelJson.transcoderUrl)
      cMap.put(_.goalPrice)(channelJson.goalPrice)
      cMap.put(_.minPrice)(channelJson.minPrice)
      cMap.tryUpdate(id)
    }
  }
}
