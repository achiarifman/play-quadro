package models.entity

import spray.json._


/**
 * Created by barcelona on 2/21/15.
 */
case class CallbackMedia(val id : String,
                         val programmeId : String,
                         val mediaUrl : String,
                         val numOfAds : Int,
                         val status : String) {

}

object CallbackMediaSerializer extends DefaultJsonProtocol{

  implicit val callbackMediaJsonFormat : JsonFormat[CallbackMedia]= jsonFormat5(CallbackMedia)
}
