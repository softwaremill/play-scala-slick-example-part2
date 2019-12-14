package controllers

import cats.effect.{Effect, IO}
import play.api.mvc.{Action, ActionBuilder, Result}

object IOHttp {

  implicit class ActionBuilderOps[+R[_], B](ab: ActionBuilder[R, B]) {

    import cats.effect.implicits._

    def asyncF[F[_] : Effect](cb: R[B] => F[Result]): Action[B] = ab.async { c =>
      cb(c).toIO.unsafeToFuture()
    }
  }
}


