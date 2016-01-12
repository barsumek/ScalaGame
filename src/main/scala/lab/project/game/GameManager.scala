package lab.project.game

import com.github.dunnololda.scage.ScageLib._
import lab.project.game.Game._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * Spawns enemies, counts score and handles interface(pausing, quiting etc).
 */
object GameManager {

  //score achieved by destroying enemies
  private var score = 0

  //list of enemy names to spawn from Enemy companion object(factory)
  val enemyNames = Array("Asteroid", "TieFighter", "TieInterceptor", "TieAdvanced")
  //holds references to spawned enemy to count points for killing them
  val enemies = new ArrayBuffer[Enemy]()

  val rand = new Random()

  import lab.project.game.Direction.Left

  actionDynamicPeriod(1000) {
    //Spawn random enemy in random place on the right edge of the window
    //the enemy is not spawned on the top/bottom edge(-60, +30 in random)
    enemies += Enemy.apply(
      enemyNames(rand.nextInt(enemyNames.length)),
      Vec(windowWidth - 1, rand.nextInt(windowHeight - 60) + 30),
      Left
    )
  }

  //clears dead enemies, adds score and pauses the game if the player died
  action {
    clearDead()
    if (!Player.alive) {
      pause()
    }
  }

  //controls used to stop, restart and quit the game
  keyIgnorePause(KEY_ESCAPE, onKeyDown = switchPause())
  keyOnPause(KEY_Q, onKeyDown = stopApp())
  keyOnPause(KEY_R, onKeyDown = {restart(); pauseOff()})

  //displays score and info about controls or lost game
  interface {
    print("SCORE: " + score, 10, windowHeight - 30, ROYAL_BLUE)
    if (onPause) {
      if (!Player.alive) {
        print("GAME OVER", windowCenter - Vec(60, 0), RED)
      } else {
        print("Use keys to move and space to shoot",
          windowCenter - Vec(150, 0), RED)
      }
      print("Press Q to exit or R to restart.",
        windowCenter - Vec(120, 30), RED)
    }
  }

  //clears score and enemies on game restart
  clear {
    score = 0
    enemies.clear()
  }

  /**
   * Removes enemies not longer in the game from ArrayBuffer.
   * Adds points to score for each one killed by the player.
   */
  private def clearDead(): Unit = {
    //find enemies killed by player
    val killedEnemies = enemies.filter(_.health < 1)
    //add score for these enemies
    killedEnemies.foreach(score += _.score)
    //remove killed and dead(out of window) enemies
    enemies --= killedEnemies
    enemies --= enemies.filter(!_.alive)
  }
}
