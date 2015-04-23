import models.actors.RecordActor
import models.message.TriggerRecordMessage
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

/**
 * Created by barcelona on 3/21/15.
 */
@RunWith(classOf[JUnitRunner])
class RecordActorTest extends BaseActorTest[RecordActor] {

  describe("Test Record Actor") {

    it("Should send record trigger") {
      val message = TriggerRecordMessage("550d496b03645e214f2f54f2","http://46.249.213.87/iPhone/broadcast/tracetvsports-tablet.3gp.m3u8",
      120,"sport","745")
      tester.send(actorRef,message)
    }
  }


}
