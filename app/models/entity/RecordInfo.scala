package models.entity

import spray.json.DefaultJsonProtocol

/**
 * Created by barcelona on 3/17/15.
 */
case class RecordInfo(val url : String,
                      val duration : Long,
                      val channelName : String,
                      val adsPaths : List[String],
                      val callBackUrl : String,
                      val programId : String) {

}

object RecordInfoSerializer extends DefaultJsonProtocol {

  implicit val recordInfoFormat = jsonFormat6(RecordInfo)
}
