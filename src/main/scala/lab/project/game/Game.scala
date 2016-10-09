package lab.project.game

import com.github.dunnololda.scage.ScageLib._
import org.newdawn.slick.openal.{SoundStore, AudioLoader}
import org.newdawn.slick.util.ResourceLoader

/**
 * Screen Object and main game loop.
 * 800x600 resolution.
 * Bottom left corner is (0, 0)!
 */
object Game extends ScageScreenApp("Game", 800, 600) {

  // traces all game objects and moves them
  val tracer = new CoordTracer[GameObject](0, windowWidth,
    0, windowHeight, solid_edges = true)

  //background image
  private val background = image("background.jpg", 800, 600, 0, 0, 1920, 1080)

  //loads music, unfortunately only OGG seems to be supported, not WAV/MP3
  private val oggStream = AudioLoader.getStreamingAudio("OGG",
    ResourceLoader.getResource("resources/music/background_music.ogg"))
  //starts music
  oggStream.playAsMusic(1.0f, 1.0f, true)

  //continuously plays the background music
  actionIgnorePause {
    SoundStore.get().poll(0)
  }

  //Player object handles player gameplay logic(movement, health etc)
  Player
  //spawns enemies, counts points and handles lose conditions
  GameManager

  backgroundColor = BLACK
  // -1 so the background is rendered first, before other game objects
  render(-1) {
    drawDisplayList(background, windowCenter)
  }
}

/**
 * Makes movement code more readable.
 */
object Direction extends Enumeration {
  type Direction = Value
  val Left, Right, Up, Down = Value

  import lab.project.game.Game.tracer

  /**
    * Moves traced object by speed based on given direction
    * @param dir Direction - in which the object will be moved
    * @param trace GameObject - used to trace enemy/player or missile
    * @param speed Int - value of object's speed to move him
    * @return Int - not used
    */
  def move(dir: Direction, trace: GameObject, speed: Int): Int = dir match {
    case Left => tracer.moveTrace(trace, Vec(-speed, 0))
    case Right => tracer.moveTrace(trace, Vec(speed, 0))
    case Up => tracer.moveTrace(trace, Vec(0, speed))
    case Down => tracer.moveTrace(trace, Vec(0, -speed))
  }

  /**
   * Checks if the traced object is on the edge of the window
   * @param dir Direction - in which the object moves
   * @param pos Vec - used to get current x/y location
   * @param speed Int - needed to check if the object is on the edge
   * @param width Int - width boundary, default value is windowWidth
   * @param height Int - height boundary, default value is windowHeight
   * @return True if it's on the edge
   */
  def checkBounds(dir: Direction, pos: Vec, speed: Int,
                  width : Int = windowWidth,
                  height: Int = windowHeight): Boolean = dir match {
      case Right => pos.x > width - speed - 1
      case Left => pos.x < speed + 1
      case Up => pos.y > height - speed - 1
      case Down => pos.y < speed + 1
    }
}

/**
 * Traces GameObjects(ships and missiles) and
 * makes interactions between them.
 */
trait GameObject extends TraceTrait {
  type ChangerType = GameObject
}