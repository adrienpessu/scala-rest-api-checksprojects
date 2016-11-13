package models

import play.api.libs.json.Json

case class User(name: String
                , nickname: String
                , familyName: String
                , userId: String
                , email: String
                , emailVerified: String
                , userMetadata: String
               )

object User {
  implicit val jsonFormatter = Json.format[User]
}
