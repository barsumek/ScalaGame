package lab.project.game

import com.github.dunnololda.scage.ScageLib._
import lab.project.game.Game._

/**
 * Player object.
 * Handles all of player's game logic, which includes displaying,
 * moving, shooting and colliding with other game objects.
 */
object Player extends ShootsBolts {
  //image to display as the player
  private val playerShip = image("player_ship.png", 60, 40, 0, 0, 325, 227)
  //tag used to differ player from enemies in case of collisions
  val tag = "Player"

  //used to move player and detect collisions
  val trace = new GameObject {
    val state = new State(tag)

    //lose 1 shield if hit, or 5 if crashed into an enemy
    override def changeState(changer: GameObject, state: State): Unit = {
      state.neededKeys {
        case ("hit", true) => shieldsDrop()
        case ("crash", true) => (1 to 5).foreach(_ => shieldsDrop())
      }
    }
  }

  //add game object trace to the main coordinate tracer
  tracer.addTrace(windowCenter, trace)

  var alive = true
  private var shields = 10
  private val speed = 5

  //used for restarting the game
  init {
    tracer.updateLocation(trace, windowCenter)
    alive = true
    shields = 10
  }

  import Direction._

  //player controls - moving
  key(KEY_LEFT,  10, onKeyDown = move(Left, trace, speed))
  key(KEY_RIGHT, 10, onKeyDown = move(Right, trace, speed))
  key(KEY_UP, 10, onKeyDown = move(Up, trace, speed))
  key(KEY_DOWN, 10, onKeyDown = move(Down, trace, speed))

  //shooting missiles
  key(KEY_SPACE, 250, onKeyDown =
    shootBolt(trace.location, Right, tag))

  //player and enemies are rendered above background and missiles
  render(1) {
    openglMove(trace.location)
    drawDisplayList(playerShip, Vec.zero)
  }

  //displays amount of shields left
  interface {
    print("SHIELDS POWER: " + shields.toString, 10, 20, LIGHT_SKY_BLUE)
  }

  /**
   * If player has shields it drops them by 1,
   * otherwise player dies and it's game over.
   */
  private def shieldsDrop(): Unit = {
    if (shields > 0) {
      shields -= 1
    } else {
      alive = false
    }
  }

}
