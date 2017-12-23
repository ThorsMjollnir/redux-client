package uk.ac.ncl.openlab.intake24.redux

import io.circe.Decoder.Result
import io.circe._

object ReduxSumType {
  val typeKey = "type"
}

class ReduxSumTypeDecoder[T](val typePrefix: String = "")(implicit val wrappedDecoder: Decoder[T]) extends Decoder[T] {

  def convertToCirce(json: Json): Option[Json] = {

    /*
    Transform incoming JSON from

    {
      "type": "TypeName", "field1": ... "field2": ... }
    }

    to

    {
      "TypeName" : { "field1": ... "field2": ... }
    }
    */

    val cursor = json.hcursor

    for (
      typeName <- cursor.get[String](ReduxSumType.typeKey).toOption;
      wrappedFields <- cursor.downField(ReduxSumType.typeKey).delete.focus
    ) yield
      Json.fromJsonObject(JsonObject.singleton(typeName.substring(typePrefix.length), wrappedFields))
  }

  override def apply(c: HCursor): Result[T] = {
    val cursor = for (json <- c.focus;
                      converted <- convertToCirce(json))
      yield converted.hcursor

    cursor match {
      case None => Left(DecodingFailure("Sum type object is not in the expected format", c.history))
      case Some(cursor) => wrappedDecoder(cursor)
    }
  }
}

class ReduxSumTypeEncoder[T](typePrefix: String = "")(implicit val wrappedEncoder: Encoder[T]) extends Encoder[T] {

  def convertToRedux(json: Json): Json = {
    /*
    Transform incoming JSON from

    {
      "TypeName" : { "field1": ... "field2": ... }
    }

    to

    {
      "type": "TypeName", "field1": ... "field2": ... }
    }
    */

    val rootObject = json.asObject.get

    val (typeName, wrappedObject) = rootObject.toList.head

    Json.fromJsonObject((ReduxSumType.typeKey -> Json.fromString(typePrefix + typeName)) +: wrappedObject.asObject.get)
  }

  override def apply(a: T): Json = {
    convertToRedux(wrappedEncoder(a))
  }
}
