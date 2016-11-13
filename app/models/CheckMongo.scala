package models

import play.api.libs.json.Json

/**
  * Created by adrien on 13/11/16.
  */
case class CheckMongo(ip: String, device: String ,message: String, user: User)

object CheckMongo {
  implicit val formatter = Json.format[CheckMongo]
}

