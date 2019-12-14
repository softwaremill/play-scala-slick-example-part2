package repositories

import models.Person

trait PersonRepository[F[_]] {

  def get(name: String): F[Option[Person]]

  def create(name: String, age: Int): F[Person]

  def list(): F[Seq[Person]]

}
