package lab.project.game

import com.github.dunnololda.scage.ScageLib._
import lab.project.game.Game._

/**
 *  Creates animation in given place.
 *  Handles changing frames as build-in animation class
 *  does the same, does not support gif and seems to be quite bugged.
 */
trait Animation {

  //needs to be implemented
  val position: Vec //position where animation should be displayed
  val animationFrames : Array[Int] //array of loaded animation frames

  //used to change frames
  private var frame = 0

  actionDynamicPeriod(50) {
    frame += 1
    //end of animation
    if (frame >= animationFrames.length) {
      deleteSelf()
    }
  }

  //animations are rendered above everything else
  render(2) {
    if (frame >= animationFrames.length) {
      deleteSelf()
    } else {
      drawDisplayList(animationFrames(frame), position)
    }
  }

  //used to destroy animation on the game restart
  clear {
    frame = animationFrames.length
  }
}

/**
 * Explosion animation played when an enemy is destroyed.
 * @param position Vec where explosion needs to be rendered
 */
class Explosion(val position: Vec) extends Animation {
  val animationFrames = Array(
    image("explosion1.png", 40, 40, 0, 0, 132, 150),
    image("explosion2.png", 40, 40, 0, 0, 160, 182),
    image("explosion3.png", 40, 40, 0, 0, 190, 158),
    image("explosion4.png", 40, 40, 0, 0, 232, 160)
  )
}

/**
 * Missile hit animation to play when the missile collide with a ship.
 * @param position Vec where missile hit
 */
class MissileHit(val position: Vec) extends Animation {
  val animationFrames = Array(
    image("hit1.png", 20, 20, 0, 0, 40, 40),
    image("hit2.png", 20, 20, 0, 0, 28, 31),
    image("hit3.png", 20, 20, 0, 0, 32, 30)
  )
}
