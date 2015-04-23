package models.entity

import net.fwbrasil.activate.entity.EntityWithCustomID

/**
 * Created by barcelona on 2/21/15.
 */
case class User(val id : String,username : String, password : String, val programmesList : List[String] = List.empty, totalFee : BigDecimal = 0) extends EntityWithCustomID[String]{

}
