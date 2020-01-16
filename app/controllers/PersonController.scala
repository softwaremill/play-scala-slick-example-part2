package controllers

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import play.api.mvc._
import IOHttp._
import cats.effect.IO
import controllers.forms.CreatePersonForm
import services.PersonService

import scala.concurrent.ExecutionContext


class PersonController(personService: PersonService[IO],
                       messagesControllerComponents: MessagesControllerComponents
                      )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(messagesControllerComponents) {

  /**
    * The mapping for the person form.
    */
  val personForm: Form[CreatePersonForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "age" -> number.verifying(min(0), max(140))
    )(CreatePersonForm.apply)(CreatePersonForm.unapply)
  }

  /**
    * The index action.
    */
  def index = Action { implicit request =>
    Ok(views.html.index(personForm))
  }

  /**
    * The add person action.
    *
    * This is asynchronous, since we're invoking the asynchronous methods on PersonRepository.
    */
  def addPerson = Action.asyncF { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    personForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        IO.pure(Ok(views.html.index(errorForm)))
      },
      // There were no errors in the from, so create the person.
      person => {
        personService.createPerson(Person(None, person.name, person.age)).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.PersonController.index).flashing("success" -> "user.created")
        }.handleErrorWith { e =>
          IO.pure(Redirect(routes.PersonController.index).flashing("error" -> e.getMessage))
        }
      }
    )
  }

  /**
    * A REST endpoint that gets all the people as JSON.
    */
  def getPersons = Action.asyncF { implicit request =>
    personService.list().map { people =>
      Ok(Json.toJson(people))
    }
  }
}

