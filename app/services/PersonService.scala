package services

import cats.MonadError
import models.Person
import repositories.PersonRepository
import cats.implicits._

class PersonService[F[_]](personRepository: PersonRepository[F])(
  implicit monadError: MonadError[F, Throwable]
) {

  def createPerson(person: Person): F[Person] =
    for {
      _ <- monadError.fromEither(validatePerson(person))
      mayBeUser <- personRepository.get(person.name)
      _ <- mayBeUser.fold(person.pure[F])(_ => monadError.raiseError(new IllegalArgumentException("User with the given name already exist")))
      p <- personRepository.create(person.name, person.age)
    } yield p


  def list(): F[Seq[Person]] = personRepository.list()

  private def validatePerson(person: Person): Either[Throwable, Person] = {
    if (person.age >= 18) {
      Right(person)
    } else {
      Left(new IllegalArgumentException("Too young to participate"))
    }
  }

}
