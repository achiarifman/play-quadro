package models.json

import play.api.libs.json.Json

/**
 * Created by barcelona on 2/13/15.
 */
case class ChannelJson(name : String,
                       number : Int,
                       epgId : String,
                       recordingEnable : Boolean,
                       url : String,
                       transcoderUrl : String,
                       ads : List[String],
                       val goalPrice : BigDecimal,
                       val minPrice : BigDecimal
                        ) {

}

object ChannelJson{

  implicit val channelJsonFormat = Json.format[ChannelJson]
}
