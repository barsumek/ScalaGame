package lab.project.game

import com.github.dunnololda.scage.ScageLib._
import lab.project.game.Game._
import lab.project.game.Direction._

/**
 * Enemy abstract class used to make new enemies.
 * Has implemented all of basic enemy logic like moving,
 * colliding and losing health.
 * @param start Vec - starting position
 * @param dir Direction - direction in which the enemy moves
 */
abstract class Enemy(val start: Vec, val dir: Direction) {

  //fields which need to be implemented in inheriting classes
  val enemyImage: Int //image used to display and differ enemies
  val speed: Int //speed of the enemy for trace movement
  val rotateAng: Int //should be 0 if enemy does not rotate
  val score: Int //score for destroying this enemy by Player
  var health : Int //enemy health, decremented in case of missile hit

  //false if the enemy should be removed from the game window
  var alive = true
  //used rotate the enemy if rotateAng is not zero
  private var ang = 0

  //used to differ enemies from player
  val tag = "Enemy"

  //used for moving the enemy and colliding
  val trace = new GameObject {
    val state = new State(tag)

    //loses health if hit by player
    override def changeState(changer: GameObject, state: State): Unit = {
      state.neededKeys {
        case ("hit", true) => loseHealth()
      }
    }
  }

  //add the trace to the main coordinate tracer
  tracer.addTrace(start, trace)

  //enemy logic loop
  action {
    //if alive then move and check collisions
    if (health > 0 && alive) {
      move(dir, trace, speed)
      ang += rotateAng
      //checks if the enemy is not at the edge of the map
      if (checkBounds(dir, trace.location, speed)) {
        alive = false
      } else {
        // find player if he collides with enemy, which results in crash
        val playerCrash = tracer.tracesNearCoord(trace.location, -1 to 1,
          target_trace => {
            target_trace.state.contains("Player") &&
            target_trace.location.dist(trace.location) < 35f
          }
        )
        //player crashed into the enemy
        if (playerCrash.nonEmpty) {
          playerCrash.foreach(_.changeState(trace, new State("crash")))
          crash()
        }
      }
    } else {
      //if health <= 0, which means player shot down the enemy
      if (alive) {
        crash()
      }
      tracer.removeTrace(trace)
      deleteSelf()
    }
  }

  //the enemy is rendered above the missiles.
  render(1) {
    if (alive) {
      openglMove(trace.location)
      openglRotate(ang)
      drawDisplayList(enemyImage, Vec.zero)
    } else {
      deleteSelf()
    }
  }

  //destroys the enemy on game restart
  clear {
    alive = false
  }

  /**
    * Drops enemy's health by 1, makes balancing the game quite easy
    */
  private def loseHealth(): Unit = health -= 1

  /**
   * Displays explosion animation and destroys the enemy.
   * Used if the player crashed into the enemy or
   * the enemy was shot down by the player.
   */
  private def crash(): Unit = {
    new Explosion(trace.location)
    alive = false
  }
}

/**
 * Rotating asteroid. Player can only crash into it.
 * @param flyStart Vec - starting position
 * @param flyDir Direction - in which the enemy moves
 */
class Asteroid(flyStart: Vec, flyDir: Direction)
    extends Enemy(flyStart, flyDir) {
  val enemyImage = image("asteroid.png", 70, 60, 0, 0, 137,124)
  val speed = 2
  //randomized asteroid rotation for better game variety
  import scala.util.Random
  val rotateAng = new Random().nextInt(10)
  val score = 5
  var health = 3
}

/**
 * Basic enemy ship. One weapon and medium health.
 * @param flyStart Vec - starting position
 * @param flyDir Direction - in which the enemy moves
 */
class TieFighter(flyStart: Vec, flyDir: Direction)
    extends Enemy(flyStart, flyDir) with ShootsBolts {
  val enemyImage = image("tie_fighter.png", 70, 60, 0, 0, 374, 336)
  val speed = 4
  val rotateAng = 0
  val score = 10
  var health = 2

  actionDynamicPeriod(2000) {
    if (alive) {
      shootBolt(trace.location, Left, tag)
    } else {
      deleteSelf()
    }
  }
}

/**
 * The fastest ship with two weapons, but with small amount of health.
 * @param flyStart Vec - starting position
 * @param flyDir Direction - in which the enemy moves
 */
class TieInterceptor(flyStart: Vec, flyDir: Direction)
    extends Enemy(flyStart, flyDir) with ShootsLongBolts {
  val enemyImage = image("tie_interceptor.png", 70, 60, 0, 0, 374, 336)
  val speed = 5
  val rotateAng = 0
  val score = 20
  var health = 1

  //this ship has two weapons and fires one after another
  val weaponDiff = Vec(0, 30)
  var left = true

  actionDynamicPeriod(1000) {
    if (alive) {
      if (left) {
        shootLongBolt(trace.location - weaponDiff, Left, tag)
        left = false
      } else {
        shootLongBolt(trace.location + weaponDiff, Left, tag)
        left = true
      }
    } else {
      deleteSelf()
    }
  }
}

/**
 * The most dangerous ship. A lot of health and three weapons.
 * @param flyStart Vec - starting position
 * @param flyDir Direction - in which the enemy moves
 */
class TieAdvanced(flyStart: Vec, flyDir: Direction)
    extends Enemy(flyStart, flyDir) with ShootsLongBolts with ShootsBolts {
  val enemyImage = image("tie_advanced.png", 70, 60, 0, 0, 374, 336)
  val speed = 2
  val rotateAng = 0
  val score = 50
  var health = 5

  //this ship has two weapons and fires them simultaneously
  val weaponDiff = Vec(0, 30)

  actionDynamicPeriod(3000) {
    if (alive) {
      shootLongBolt(trace.location - weaponDiff, Left, tag)
      shootLongBolt(trace.location + weaponDiff, Left, tag)
    } else {
      deleteSelf()
    }
  }

  //it also has normal bolt cannon in center which fires pretty fast
  actionDynamicPeriod(1000) {
    if (alive) {
      shootBolt(trace.location, Left, tag)
    } else {
      deleteSelf()
    }
  }
}

/**
 * Enemy factory using companion object.
 * Returns Enemy based on given name instantiated in specified location
 */
object Enemy {
  /**
   * Factory method.
   * @param name String - Enemy class name
   * @param start Vec - position where to spawn the enemy
   * @param dir Direction - moving direction
   * @return Enemy - an instantiated Enemy or null if wrong name
   */
  def apply(name: String, start: Vec, dir: Direction): Enemy = name match {
    case "Asteroid" => new Asteroid(start, dir)
    case "TieFighter" => new TieFighter(start, dir)
    case "TieInterceptor" => new TieInterceptor(start, dir)
    case "TieAdvanced" => new TieAdvanced(start, dir)
    case _ => null
  }
}

