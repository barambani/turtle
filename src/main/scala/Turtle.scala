import scala.annotation.tailrec
import scalaz.{\/, -\/, \/-}

object Turtle {

  object model {
  
    sealed trait Move
    final case object Move extends Move

    sealed trait Rotate
    final case object Rotate extends Rotate

    final case class Tile(x: Int, y: Int)
    final case class Position(tile: Tile, dir: Direction)
    final case class Limits(maxX: Int, maxY: Int)

    type Land = (Limits, Map[Tile, Outcome])
    type ActionEffect = \/[Rotate,  Move]
    type Actions = (String, Seq[ActionEffect])

    sealed trait Outcome extends Product with Serializable
    final case object Success extends Outcome
    final case object Boom extends Outcome
    final case object StillInDanger extends Outcome
    final case object OutsideTheLimits extends Outcome

    sealed trait Direction extends Product with Serializable
    final case object North extends Direction
    final case object East extends Direction
    final case object South extends Direction
    final case object West extends Direction
    
    lazy val move: Position => Position = 
      p => p.copy(tile = p match {
        case Position(Tile(x, y), North)  => Tile(x, y - 1)
        case Position(Tile(x, y), East)   => Tile(x + 1, y)
        case Position(Tile(x, y), South)  => Tile(x, y + 1)
        case Position(Tile(x, y), West)   => Tile(x - 1, y)
      })

    lazy val rotate: Position => Position =
      p => p.copy(dir = p.dir match {
        case North  => East
        case East   => South
        case South  => West
        case West   => North
      })
  }

  import model._

  private lazy val checkPosition: Land => Position => \/[Outcome, Position] = 
    land => pos => {
      val (limits, tiles) = land
      if(pos.tile.x < 0 
        || pos.tile.x > limits.maxX 
        || pos.tile.y < 0 
        || pos.tile.y > limits.maxY)    -\/(OutsideTheLimits)
      else if(tiles.contains(pos.tile)) -\/(tiles(pos.tile))
      else                              \/-(pos)
    }
  
  private lazy val execAction: ActionEffect => Position => Position =
    effect => pos => effect.fold(
      _ => rotate(pos),
      _ => move(pos)
    )

  private lazy val moveAndCheck: Land => ActionEffect => Position => \/[Outcome, Position] =
    land => effect => checkPosition(land) compose execAction(effect)

  @tailrec
  private def runRec(p: Position)(xs: Seq[ActionEffect])(l: Land): \/[Outcome, Position] =
    if(xs.isEmpty) -\/(StillInDanger)
    else moveAndCheck(l)(xs.head)(p) match {
      case \/-(op)    => runRec(op)(xs.tail)(l)
      case r @ -\/(_) => r
    }

  lazy val run: Position => Seq[ActionEffect] => Land => Outcome =
    start => xs => l => runRec(start)(xs)(l).fold(identity, _ => StillInDanger) 
}

