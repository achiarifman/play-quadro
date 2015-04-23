package models.message

/**
 * Created by barcelona on 2/20/15.
 */
class ScheduleMessages {

}

case class ScheduleRecordingsMessage() {}

case class PullEpgMessage() {}

case class TriggerRecordMessage(id : String, url : String, duration : Long, name : String, channelEpgId : String) {}

case class DeleteVodMessage(id : String)

case class RemoveUnSavedVods(days : Int)

case class RemoveForceVods(days : Int)

case class CalculateFeesMessage()

