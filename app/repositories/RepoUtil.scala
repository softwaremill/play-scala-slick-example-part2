package repositories

import cats.effect.Effect
import cats.implicits._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object RepoUtil {

  def fromFuture[F[_], A](f: => Future[A])(implicit F: Effect[F], ec: ExecutionContext): F[A] =
    F.delay(f) >>= (
      f => F.async { cb =>
        f.onComplete {
          case Success(a) => cb(Right(a))
          case Failure(ex) => cb(Left(ex))
        }
      })

}
