package repositories

import cats.Applicative
import models.Person
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import cats.effect.{Effect, IO}
import cats.implicits._
import slick.basic.DatabaseConfig

import scala.concurrent.ExecutionContext

class SlickPersonRepository[F[_] : Effect](dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext)
  extends PersonRepository[F] {

  implicit val cs = IO.contextShift(ec)

  import dbConfig._
  import profile.api._

  private class PeopleTable(tag: Tag) extends Table[Person](tag, "people") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def age = column[Int]("age")

    def * = (id.?, name, age) <> ((Person.apply _).tupled, Person.unapply)
  }

  private val people = TableQuery[PeopleTable]

  override def get(name: String): F[Option[Person]] = {
    RepoUtil.fromFuture(db.run {
      people.filter(_.name === name).result.headOption
    })
  }

  override def create(name: String, age: Int): F[Person] = {
    RepoUtil.fromFuture(db.run {
      (people.map(p => (p.name, p.age))
        returning people.map(_.id)
        into ((nameAge, id) => Person(Some(id), nameAge._1, nameAge._2))
        ) += (name, age)
    })
  }

  override def list(): F[Seq[Person]] = {
    RepoUtil.fromFuture(db.run(people.result))
  }

}
