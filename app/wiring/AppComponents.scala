package wiring

import com.softwaremill.macwire._
import _root_.controllers.{Assets, AssetsComponents, PersonController}
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.{BuiltInComponents, BuiltInComponentsFromContext, NoHttpFiltersComponents}
import router.Routes
import play.api.mvc._
import services.PersonService
import cats._
import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import play.api.db.HikariCPComponents
import play.api.db.evolutions.EvolutionsComponents
import play.api.i18n.I18nComponents
import play.filters.HttpFiltersComponents

import scala.concurrent.ExecutionContext

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with BuiltInComponents
  with AssetsComponents
  with I18nComponents
  with HttpFiltersComponents
  with DatabaseComponents {

  implicit val ec: ExecutionContext = executionContext

  private lazy val personService: PersonService[IO] = wire[PersonService[IO]]

  /*
   defaultActionBuilder,
    playBodyParsers,
    messagesApi,
    langs,
    fileMimeTypes,
    executionContext
   */
  private lazy val messagesActionBuilder = new DefaultMessagesActionBuilderImpl(playBodyParsers.defaultBodyParser, messagesApi)
  private lazy val messagesControllerComponents = DefaultMessagesControllerComponents(
    messagesActionBuilder,
    defaultActionBuilder,
    playBodyParsers,
    messagesApi,
    langs,
    fileMimeTypes,
    ec)

  private lazy val personController = new PersonController(personService, messagesControllerComponents)

  override def router: Router = {
    val routePrefix: String = "/"
    wire[Routes]
  }
}

