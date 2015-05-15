package models.entity

import net.fwbrasil.activate.entity.{EntityWithCustomID, Entity}
import spray.json.DefaultJsonProtocol

/**
 * Created by barcelona on 2/13/15.
 */
case class Channel(val id : String,
              val name : String,
              val number : Int,
              val epgId : String,
              val recordingEnable : Boolean,
              val url : String,
              val transcoderUrl : Option[String],
              val ads : List[String],
              val goalPrice : Option[BigDecimal],
              val minPrice : Option[BigDecimal]
) extends EntityWithCustomID[String]{

}




/*
object ChannelSerializer extends DefaultJsonProtocol {

  implicit val channelFormat = jsonFormat7(Channel)
}*/
