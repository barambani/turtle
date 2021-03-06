import Turtle._
import Turtle.model._
import fs2._
import java.nio.file.Paths
import scalaz.{\/,-\/,\/-}
import scalaz.Scalaz._

object TurtleRun {

  implicit val strategy = Strategy.fromFixedDaemonPool(10)

  def main(args: Array[String]): Unit = {

    import fileIo._
    import parsers._

    lazy val sequences: Stream[Task, Option[Actions]] = 
      fileRows("src/main/resources/moves.txt") map parseSequence

    // The IO here is similar to the sequence's. I'm mocking those two
    lazy val initial: Stream[Task, Position] = 
      Stream.emit[Task, Position](Position(Tile(0,2), North))
    
    lazy val land: Stream[Task, Land] =
      Stream.emit[Task, Land](
        (Limits(10,7), Map[Tile, Outcome](Tile(2,3) -> Boom, Tile(4,5) -> Boom, Tile(6,2) -> Boom, Tile(9,5) -> Success))
      )

    
    lazy val printOption: Option[String] => Unit =
      x => println(x.getOrElse("Nothing to show"))

    lazy val resultMessage: Position => Land => Actions => String = 
      start => l => xs => {
        lazy val (name, actions) = xs
        s"${name}: ${Turtle.run(start)(actions)(l)}"
      }

    lazy val resultStreamFor: Position => Land => Stream[Task, Option[String]] =
      start => land => sequences map { _ map resultMessage(start)(land) }

    lazy val results: Stream[Task, Option[String]] = 
      (initial zip land) flatMap { case (start, land) => resultStreamFor(start)(land) }

    (results map printOption).run.unsafeRun
  }

  object fileIo {

    lazy val fileRows: String => Stream[Task, String] =
      path => io.file
        .readAll[Task](Paths.get(path), 4096)
        .through(text.utf8Decode)
        .through(text.lines)
        .filter(!_.trim.isEmpty)
  }

  object parsers {

    val parseSequence: String => Option[Actions] = 
      row => split(row)("=") match {
        case n :: es :: Nil => (n, parseEffects(es).flatten).some
        case _              => none
      }
    
    private val parseEffects: String => Seq[Option[ActionEffect]] =
      es => split(es)(",") map (parseEffect.lift)

    private val parseEffect: PartialFunction[String, ActionEffect] = {
      case "Move"   => \/-(Move)
      case "Rotate" => -\/(Rotate)
    }

    private lazy val split: String => String => List[String] =
      s => pattern => (s split pattern).toList
  }
}
