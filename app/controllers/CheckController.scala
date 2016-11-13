package controllers

import javax.inject._

import models.{Check, CheckMongo, User}
import play.Logger
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo._
import reactivemongo.api.ReadPreference
import reactivemongo.play.json.collection._
import play.modules.reactivemongo.json._, ImplicitBSONHandlers._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckController @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit exec: ExecutionContext) extends Controller with MongoController with ReactiveMongoComponents with Secured {

  def checksFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection]("checks"))

  def logging[A](action: Action[A]) = Action.async(action.parser) { request =>
    Logger.debug("Calling action")
    action(request)
  }

  def create = AuthenticatedAction.async(parse.json) {
    authenticatedRequest => {
      Json.fromJson[Check](authenticatedRequest.body) match {
        case JsSuccess(check, _) =>
          for {
            repositories <- checksFuture
            lastError <- repositories.insert(joinChecksUser(check, authenticatedRequest.user))
          } yield {
            Logger.debug("Created 1 check from json");
            Created(authenticatedRequest.body.toString())
          }
        case JsError(errors) =>
          Future.successful(BadRequest("Could not build a check from the json provided. "))
      }

    }
  }

  def list() = AuthenticatedAction.async {
    authenticatedRequest => {
      // let's do our query
      val futureChecksList: Future[List[Check]] = checksFuture.flatMap {
        // find all cities with name `name`
        _.find(Json.obj("" -> "")).
          // perform the query and get a cursor of JsObject
          cursor[Check](ReadPreference.primary).
          // Coollect the results as a list
          collect[List]()
      }

      // everything's ok! Let's reply with a JsValue
      futureChecksList.map { checks =>
        Ok(Json.toJson(checks))
      }
    }
  }

  def joinChecksUser(check:Check, user:User) : CheckMongo = {
    CheckMongo(check.ip, check.device, check.message, user)
  }

  def hello = AuthenticatedAction {
    Ok(Json.toJson("{Hello: 'World'}"))
  }

  def helloDude = AuthenticatedAction { authenticatedRequest =>
    Ok(Json.toJson("{Hello: '" + authenticatedRequest.user.name + "'}"))
  }

}


