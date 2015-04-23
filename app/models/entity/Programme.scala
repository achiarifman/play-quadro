package models.entity

import net.fwbrasil.activate.entity.{EntityWithCustomID, Entity}

/**
 * Created by barcelona on 2/13/15.
 */
class Programme(val id : String,
                val channelEpgId : String,
                val title : String,
                val startTime : Long,
                val endTime : Long,
                val url : String,
                val goalPrice : BigDecimal,
                val price : BigDecimal,
                var savedCounter : Int = 0,
                var isScheduled : Boolean = false
                      ) extends EntityWithCustomID[String]{

  var recordedUrl : String = _
  var isRecorded = false

}

case class ProgrammeElement(val channelEpgId : String,
                            val title : String,
                            val startTime : Long,
                            val endTime : Long,
                            val url : String)

case class PriceHolder(val goalPrice : BigDecimal,
                       val price : BigDecimal)
