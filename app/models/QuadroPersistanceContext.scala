package models


import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.play.ActivatePlayContext
import net.fwbrasil.activate.storage.mongo.MongoStorage

/**
 * Created by barcelona on 2/10/15.
 */
object QuadroPersistanceContext extends ActivateContext{

  override val storage = new MongoStorage {
    val host = "localhost"
    override val port = 27017
    val db = "quadro"
    //override val authentication = Option("local", "vidmind12")
  }
}
