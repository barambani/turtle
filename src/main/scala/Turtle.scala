import scala.annotation.tailrec
import scalaz.{\/, -\/, \/-}

object Turtle {

  object model {
  
    final case class Move()
    final case class Rotate()

    final case class Tile(x: Int, y: Int)
    final case class Position(tile: Tile, dir: Direction)

    final case class Limits(maxX: Int, maxY: Int)

    type Land = (Limits, Map[Tile, Outcome])
    type ActionEffect = Rotate \/ Move
    type Actions = (String, Seq[ActionEffect])

    sealed trait Outcome extends Product with Serializable
    case object Success extends Outcome
    case object Boom extends Outcome
    case object StillInDanger extends Outcome
    case object OutsideTheLimits extends Outcome

    sealed trait Direction extends Product with Serializable
    case object North extends Direction
    case object East extends Direction
    case object South extends Direction
    case object West extends Direction
    
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
    effect => pos => effect match {
      case \/-(_) => move(pos)
      case -\/(_) => rotate(pos)
    }

  private lazy val moveAndCheck: Land => ActionEffect => Position => \/[Outcome, Position] =
    land => effect => checkPosition(land) compose execAction(effect)

  @tailrec
  private def runRec(i: Position)(as: Seq[ActionEffect])(l: Land): \/[Outcome, Position] =
    if(as.isEmpty) -\/(StillInDanger)
    else moveAndCheck(l)(as.head)(i) match {
      case \/-(p)     => runRec(p)(as.tail)(l)
      case r @ -\/(_) => r
    }

  lazy val run: Position => Seq[ActionEffect] => Land => Outcome =
    i => as => l => runRec(i)(as)(l).fold(o => o, _ => StillInDanger) 
}

