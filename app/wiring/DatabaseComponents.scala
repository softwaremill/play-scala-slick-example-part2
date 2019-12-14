package wiring

import cats.effect.IO
import play.api.db.slick.{DbName, SlickComponents}
import repositories.SlickPersonRepository
import slick.jdbc.JdbcProfile
import com.softwaremill.macwire._
import play.api.db.HikariCPComponents
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.evolutions.SlickEvolutionsComponents

import scala.concurrent.ExecutionContext

trait DatabaseComponents extends SlickComponents
  with EvolutionsComponents
  with HikariCPComponents
  with SlickEvolutionsComponents {
  implicit def ec: ExecutionContext
  lazy val dbConfig = slickApi.dbConfig[JdbcProfile](DbName("default"))
  lazy val personRepository = wire[SlickPersonRepository[IO]]
}
