package controllers

import com.google.common.io.BaseEncoding

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import models.User
import play.api.Logger
import play.mvc.Http.HeaderNames._
import com.auth0.jwt.JWTVerifier

class AuthenticatedRequest[A](val user: User, val request: Request[A]) extends WrappedRequest[A](request)
class AuthenticatedAsyncRequest[A](user: User, request: Request[A]) extends WrappedRequest[A](request)

trait Secured {
  def SecuredAction = AuthenticatedAction
}

object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {

  def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]) ={
    val auth = request.headers.get(AUTHORIZATION)
    if(auth.isDefined) {
      val token = auth.get.substring(6).trim
      val secret = BaseEncoding.base64Url.omitPadding.decode("password")

      val verifier: JWTVerifier = new JWTVerifier(secret, "", "");
      val claims = verifier.verify(token)

      val user = User(claims.get("name").toString,
        claims.get("nickname").toString,
        claims.get("family_name").toString,
        claims.get("user_id").toString,
        claims.get("email").toString,
        claims.get("email_verified").toString,
        claims.get("user_metadata").toString
      )

      Logger.info("user" + user)
      block(new AuthenticatedRequest[A](user, request))
    }
    else{
      block(new AuthenticatedRequest[A](User("Anonymous","", "", "", "", "", ""), request))
    }
  }
}


