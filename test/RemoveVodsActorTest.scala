import models.actors.RemoveVodsActor
import models.message.{RemoveUnSavedVods, TriggerRecordMessage}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

/**
 * Created by barcelona on 4/18/15.
 */
@RunWith(classOf[JUnitRunner])
class RemoveVodsActorTest extends BaseActorTest[RemoveVodsActor]{


  describe("Test Record Actor") {

    it("Should send record trigger") {
      val message = RemoveUnSavedVods(3)
      tester.send(actorRef,message)
    }
  }
}
