package models.utils

/**
 * Created by barcelona on 2/20/15.
 */
object PropsConsts {

  val EPG_SCHEDULE = "epg.quartz.regex"
  val DAILY_SCHEDULE = "daily.quartz.regex"
  val EPG_SCHEDULE_DEFAULT = "0 0 0 1/1 * ? *"
  val DAILY_SCHEDULE_DEFAULT = "0 0 0 1/1 * ? *"
  val REMOVE_NOT_SAVED = "remove.notsaved.days"
  val REMOVE_NOT_SAVED_DEFAULT = 3
  val REMOVE_FORCE = "remove.force.days"
  val REMOVE_FORCE_DEFAULT = 30
  val HOST_IP = "callback.ip"
  val DROPBOX_PATH = "dropbox.relative.path"
  val GMT_DIFF = "gmt.diff"
}
