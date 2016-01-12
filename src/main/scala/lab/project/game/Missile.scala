package lab.project.game

import com.github.dunnololda.scage.ScageLib._
import lab.project.game.Game._
import lab.project.game.Direction._

/**
 * An abstract class used to make new missiles.
 * Has basic missile logic implemented - moving, colliding
 * and destroying after collision.
 * @param start Vec - where the missile should be spawned
 * @param dir Direction - in which the missile fly
 */
abstract class Missile(val start: Vec, val dir: Direction) {

  //needs to be implemented in inheriting classes
  val missileImage: Int //image to display as the missile
  val speed: Int //speed of the missile
  val target: String //target for the missile(Player/Enemy)

  //used for moving the missile and checking collisions
  val trace = new GameObject {
    val state = new State("Missile")

    override def changeState(changer: GameObject, state: State): Unit = {
      state.neededKeys {
        case _ => Unit
      }
    }
  }

  //adds the trace to the main coordinate tracer
  tracer.addTrace(start, trace)

  //true if the missile should not be displayed anymore
  var removeMissile = false

  //missile logic loop
  action {
    //move and check collisions, otherwise remove the missile
    if (!removeMissile) {
      move(dir, trace, speed)
      //if object should be deleted(it's on window's edge)
      if (checkBounds(dir, trace.location, speed)) {
        removeMissile = true
      } else {
        // find targets which collide with the missile
        val targets = tracer.tracesNearCoord(trace.location, -1 to 1,
          target_trace => {
            target_trace.state.contains(target) &&
            target_trace.location.dist(trace.location) < 30f
          }
        )
        //if the missile hit an enemy send "hit" and create hit animation
        if (targets.nonEmpty) {
          targets.foreach(_.changeState(trace, new State("hit")))
          new MissileHit(trace.location)
          removeMissile = true
        }
      }
    } else {
      tracer.removeTrace(trace)
      deleteSelf()
    }
  }

  //displays the missile(under player and enemies)
  render {
    if (removeMissile) {
      deleteSelf()
    } else {
      openglMove(trace.location)
      drawDisplayList(missileImage, Vec.zero)
    }
  }

  //removes the missile on the game restart
  clear {
    removeMissile = true
  }

  /**
   * Used to differ shooter from target for better game experience.
   * @param shooter String - who shot the missile.
   * @return String - the target for the missile.
   */
  def setMissileTarget(shooter: String): String = shooter match {
    case "Player" => "Enemy"
    case "Enemy" => "Player"
    case _ => "Enemy"
  }
}

/**
 * Normal bolt.
 * @param shootStart Vec - where the bolt should be spawned.
 * @param shootDir Direction - in which the bolt flies.
 * @param shooter String - shooter type(Player/Enemy)
 */
class Bolt(shootStart: Vec, shootDir: Direction, val shooter: String)
    extends Missile(shootStart, shootDir) {
  val missileImage = image("bolt.png", 14, 4, 0, 0, 14, 4)
  val speed = 7
  val target = setMissileTarget(shooter)
}

/**
 * Long bolt, faster than normal bolt.
 * @param shootStart Vec - where the bolt should be spawned.
 * @param shootDir Direction - in which the bolt flies.
 * @param shooter String - shooter type(Player/Enemy)
 */
class LongBolt(shootStart: Vec, shootDir: Direction, val shooter: String)
  extends Missile(shootStart, shootDir) {
  val missileImage = image("long_bolt.png", 25, 3, 0, 0, 50, 6)
  val speed = 9
  val target = setMissileTarget(shooter)
}

/**
 * Allows shooting normal bolts.
 */
trait ShootsBolts {
  def shootBolt(start: Vec, dir: Direction, shooter: String): Unit = {
    new Bolt(start, dir, shooter)
  }
}

/**
 * Allows shooting long bolts.
 */
trait ShootsLongBolts {
  def shootLongBolt(start: Vec, dir: Direction, shooter: String): Unit = {
    new LongBolt(start, dir, shooter)
  }
}
