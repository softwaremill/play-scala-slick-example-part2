package wiring

import com.softwaremill.macwire._
import _root_.controllers.{AssetsComponents, PersonController}
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.{BuiltInComponents, BuiltInComponentsFromContext}
import router.Routes
import play.api.mvc._
import services.PersonService
import cats.effect._
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
  private lazy val messagesActionBuilder = new DefaultMessagesActionBuilderImpl(playBodyParsers.defaultBodyParser, messagesApi)
  private lazy val messagesControllerComponents = DefaultMessagesControllerComponents(
    messagesActionBuilder,
    defaultActionBuilder,
    playBodyParsers,
    messagesApi,
    langs,
    fileMimeTypes,
    ec)

  private lazy val personController = wire[PersonController]

  override def router: Router = {
    val routePrefix: String = "/"
    wire[Routes]
  }
}

