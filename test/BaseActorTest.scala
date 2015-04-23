import akka.actor.{Actor, ActorSystem}
import akka.testkit.{TestProbe, TestActorRef, TestKit}
import org.scalatest.{Matchers, BeforeAndAfter, FunSpecLike}
import org.specs2.mutable.Specification

/**
 * Created by barcelona on 3/21/15.
 */
class BaseActorTest[T <: Actor](implicit m : scala.reflect.Manifest[T])  extends TestKit(ActorSystem("test")) with FunSpecLike with Matchers with BeforeAndAfter{


  val actorRef = TestActorRef(m.runtimeClass.newInstance().asInstanceOf[T])
  val tester = TestProbe()
}
