package models.service

import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import models.dao.{ProgrammeDao, ChannelDao}
import models.entity.{PriceHolder, ProgrammeElement, Channel}
import org.joda.time.DateTime

/**
 * Created by barcelona on 2/13/15.
 */
object XmltvToProgrammeService {


  def mapToProgramme(xmltvFile : String) = {

      val xmltv = loadXmlTvFile(xmltvFile)
      execute(xmltv)

  }

  private def execute(xmltv : scala.xml.Elem) = {

    val allSavedChannels = ChannelDao.getAllChannels

    val allPrograms = (xmltv \ "programme").map(p => {
      val channelEpgId = p.attribute("channel").get.text
      val title = p \ "title" text;
      val startTime = convertDate(p.attribute("start").get.text)
      val endTime = convertDate(p.attribute("stop").get.text)
      ProgrammeElement(channelEpgId,title,startTime.getMillis,endTime.getMillis,getUrlByChannelId(allSavedChannels,channelEpgId))
    })


    val channelsIds = (xmltv \ "channel").map(c => {
      c.attribute("id").get.text;
    })

    val requiredChannelsIds = channelsIds.filter(f => isChannelIdDefined(allSavedChannels,f))
    val channelsIdTuple = requiredChannelsIds.map(r => (getChanneldByChannelEpgId(allSavedChannels,r).get,r))
    val filterdPrograms = allPrograms.filter(p => requiredChannelsIds.contains(p.channelEpgId))
    val epgIdToChannelMap = allSavedChannels.map(channel => (channel.epgId,channel)).toMap
    val programmesTuples = filterdPrograms.map(programme => {
      val channel = epgIdToChannelMap.get(programme.channelEpgId)
      val priceHolder = PriceHolder(channel.get.goalPrice,channel.get.minPrice)
      (programme,priceHolder)
    } )
    ProgrammeDao.saveNewProgrammes(programmesTuples.toList)
  }

  def getUrlByChannelId(channels : List[Channel], id : String) = {
    val optionId = channels.find(_.epgId.equals(id))
    if(optionId.nonEmpty) optionId.get.url else "url not defined"
  }

  private def isChannelIdDefined(channels : List[Channel], channelsEpgId : String) = {
    val channelIdOption = getChanneldByChannelEpgId(channels,channelsEpgId)
    channelIdOption.isDefined
  }

  private def getChanneldByChannelEpgId(channels : List[Channel], channelsEpgId : String) = {
     channels.find(p => p.epgId.equals(channelsEpgId))
  }

  private def getChannelName(xmltv : scala.xml.Elem, id : String) : String = {
    for (channel <- xmltv \ "channel")
    {
      if (id.equals(channel.attribute("id").get.text))
      {
        return channel \ "display-name" text;
      }
    }
    "";
  }

  val df = new SimpleDateFormat("yyyyMMddHHmmss Z")

  /**
   * parse a date from the XML file into a DateTime structure
   */
  def convertDate(date : String) : DateTime =
  {
    val result = new DateTime(df.parse(date))
    result;
  }

  /**
   * write /xmltv.dtd from the classpath onto @param file
   */
  private def writeDtd(file : File)
  {
    System.err.println("Generating missing xmltv.dtd...")
    val fos = new FileOutputStream(file);
    val is = getClass.getResourceAsStream("/xmltv.dtd")

    var byte = is.read()
    while(byte >= 0)
    {
      fos.write(byte)
      byte = is.read()
    }
    fos.close()
    is.close()
  }

  /**
   * parse the file into a scala.xml element.
   *
   * this would fail when xmltv.dtd is not present. In that case, it is generated
   * before the parser is ran.
   *
   * @param inputFilename the file to load
   */
  private def loadXmlTvFile(inputFilename : String) : scala.xml.Elem =
  {
    val dtd = new File("xmltv.dtd");
    if (!dtd.exists)
    {
      writeDtd(dtd)
    }
    val xmltvFile = scala.xml.XML.loadFile(new File(inputFilename))
    xmltvFile
  }

}
