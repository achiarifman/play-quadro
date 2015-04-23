package models.entity

import net.fwbrasil.activate.entity.{EntityWithCustomID, Entity}

/**
 * Created by barcelona on 2/11/15.
 */
case class Vod(val id : String, val name : String) extends EntityWithCustomID[String] {


}

