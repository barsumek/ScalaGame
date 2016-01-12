package lab.project.game

import junit.framework._

object ScageAppTest {

  def suite: Test = {
      val suite = new TestSuite(classOf[ScageAppTest])
      suite
  }

  def main(args : Array[String]) {
      junit.textui.TestRunner.run(suite)
  }
}

/**
 * Unit test for checking game window's boundary.
 */
class ScageAppTest extends TestCase("Boundary") {

  import lab.project.game.Direction.checkBounds
  import lab.project.game.Direction.{Left, Right, Up, Down}
  import com.github.dunnololda.scage.support.Vec

  //Left bound tests
  def testLeftBoundOK(): Unit = {
    assert(checkBounds(Left, Vec(2, 30), 4, 800, 600))
  }

  def testLeftBoundKO(): Unit = {
    assert(!checkBounds(Left, Vec(6, 30), 4, 800, 600))
  }

  def testLeftBoundCorner(): Unit = {
    assert(checkBounds(Left, Vec(0, 0), 1, 800, 600))
  }

  def testLeftBoundEqual(): Unit = {
    assert(checkBounds(Left, Vec(5, 30), 5, 800, 600))
  }

  //Right bound tests
  def testRightBoundOK(): Unit = {
    assert(checkBounds(Right, Vec(795, 30), 8, 800, 600))
  }

  def testRightBoundKO(): Unit = {
    assert(!checkBounds(Right, Vec(790, 30), 8, 800, 600))
  }

  def testRightBoundCorner(): Unit = {
    assert(checkBounds(Right, Vec(800, 600), 1, 800, 600))
  }

  def testRightBoundEqual(): Unit = {
    assert(checkBounds(Right, Vec(795, 30), 5, 800, 600))
  }

  //Upper bound tests
  def testUpperBoundOK(): Unit = {
    assert(checkBounds(Up, Vec(30, 595), 9, 800, 600))
  }

  def testUpperBoundKO(): Unit = {
    assert(!checkBounds(Up, Vec(30, 590), 9, 800, 600))
  }

  def testUpperBoundCorner(): Unit = {
    assert(checkBounds(Up, Vec(800, 600), 1, 800, 600))
  }

  def testUpperBoundEqual(): Unit = {
    assert(checkBounds(Up, Vec(30, 595), 5, 800, 600))
  }

  //Bottom bound tests
  def testBottomBoundOK(): Unit = {
    assert(checkBounds(Down, Vec(30, 8), 9, 800, 600))
  }

  def testBottomBoundKO(): Unit = {
    assert(!checkBounds(Down, Vec(30, 10), 9, 800, 600))
  }

  def testBottomBoundCorner(): Unit = {
    assert(checkBounds(Down, Vec(0, 0), 1, 800, 600))
  }

  def testBottomBoundEqual(): Unit = {
    assert(checkBounds(Down, Vec(30, 5), 5, 800, 600))
  }

}
