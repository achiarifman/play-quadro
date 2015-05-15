import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, Matchers, FunSpecLike}
import org.specs2.runner.JUnitRunner
import models.QuadroPersistanceContext._
import models.dao.{ProgrammeDao, UserDAO}
/**
 * Created by barcelona on 5/8/15.
 */
@RunWith(classOf[JUnitRunner])
class UsersDaoTest extends FunSpecLike with Matchers with BeforeAndAfter{

  describe("Test Record Actor") {

    it("Should send record trigger") {


      transactional {
        val usersNavigator =  UserDAO.getAllUsers
        while(usersNavigator.hasNext){
          val usersPage = usersNavigator.next
          usersPage.foreach(user => {
            val programmesFees = user.programmesList.map(getProgrammePrice(_))
            val totalFee = programmesFees.sum
            println(user.username)
            println(totalFee)
          })
        }
      }
    }
  }
  def getProgrammePrice(id : String) : BigDecimal = {
    val programme = ProgrammeDao.getProgrammeById(id)
    val price = programme match{
      case Some(p) => p.price
      case _ => BigDecimal(0)
    }
    price
  }
}
