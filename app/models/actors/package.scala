package models

import models.utils.PropsConsts
import play.api.Play._

/**
 * Created by barcelona on 5/16/15.
 */
package object actors {

  val FOUR_HOURS = 60000 * 60 * 4 // four hours
  val GMT_DIFF = 60000 * 60 * current.configuration.getInt(PropsConsts.GMT_DIFF).getOrElse(3)
}
