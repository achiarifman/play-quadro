package models.actors

import java.io.{IOException, File}

import akka.actor.Actor
import play.api.Logger
import models.message.DeleteVodMessage
import models.utils.PropsConsts
import org.h2.store.fs.FileUtils
import play.api.Play._
import scala.util.matching.Regex

/**
 * Created by barcelona on 4/4/15.
 */
class DeleteVodActor extends Actor{

  val DROPBOX_PATH = current.configuration.getString(PropsConsts.DROPBOX_PATH).getOrElse("/Users/barcelona/Dropbox/Public/")
  val VOD_PATH = DROPBOX_PATH + "vods"

  override def receive: Receive = {
    case DeleteVodMessage(id)  =>  deleteVod(id)
  }

  def deleteVod(id : String) = {
    val regex = new Regex("(" + id + ")")
    val folder = new File(VOD_PATH)
    if(folder.exists()){
      val folderFilesList = getRecursiveListOfFiles(folder)
      val deleteFiles = folderFilesList.filter(file => regex.findFirstIn(file.getName).isDefined)
      deleteFiles.foreach(file => {
        if(file.exists()){
          try {
            val path = file.getAbsolutePath
            FileUtils.delete(path)
          }catch{
            case e : IOException => Logger.error("Could not delete the file " + file.getAbsolutePath)
          }
        }
      })
    }
  }

  def getRecursiveListOfFiles(dir: File): Array[File] = {
    val these = dir.listFiles
    if(these != null) {
      these ++ these.filter(_.isDirectory).flatMap(getRecursiveListOfFiles)
    }
    these
  }
}
