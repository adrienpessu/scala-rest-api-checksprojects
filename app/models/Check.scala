package models

import play.api.libs.json.Json

case class Check(ip: String, device: String ,message: String)

object Check {
  implicit val formatter = Json.format[Check]
}
