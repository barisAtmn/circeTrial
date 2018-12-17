import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.auto._

object Application extends App{

  val rawJson: String = """
                      {
                        "foo": "bar",
                        "baz": 123,
                        "list of stuff": [ 4, 5, 6 ]
                      }
                      """

  val doc:Json = parse(rawJson) match {
    case Left(failure) => println(s"We got Error!! $failure"); Json.Null
    case Right(json) => println(s"### Full document ### \n $json \n"); json
  }


  val cursor: HCursor = doc.hcursor

  val barR: Decoder.Result[Int] = cursor.downField("baz").as[Int]

  println(s"### Using HCursor to reach element of Json ### \n baz : ${barR.right.get} \n")


  val reversedNameCursor: ACursor =
    cursor.downField("foo").withFocus(_.mapString(_.reverse))

  println(s"### Using ACursor to modify element of Json ### \n new value of foo : ${reversedNameCursor.top.get} \n")

  // Encoding data to Json can be done using the .asJson syntax:

  val intsJson:Json = List(1, 2, 3).asJson

  println(s"Convert list[A] to Json\n $intsJson \n")

  // Use the .as syntax for decoding data from Json:

  val backToScala:Result[List[Int]] = intsJson.as[List[Int]]

  println(s"Convert Json to list[A] \n ${backToScala.right.get} \n")

  // The decode function from the included [parser] module can be used to directly decode a JSON String:

  println(s"Convert Json to list[A] \n ${decode[List[Int]](intsJson.toString())} \n")


  case class Foo(a: Int, b: String, c: Boolean)

  implicit val fooDecoder: Decoder[Foo] = deriveDecoder[Foo]
  implicit val fooEncoder: Encoder[Foo] = deriveEncoder[Foo]

  val fooInstance: Foo = Foo(27,"Baris", true)

  val fooJson :Json = fooInstance.asJson

  println(s"Convert Foo object to Json \n $fooJson \n")

  val fooBack:Decoder.Result[Foo] = fooJson.as[Foo]

  println(s"Convert Json to Foo object \n ${fooBack.right.get} \n")

  // this one needs macro
  @JsonCodec
  case class Bar(i: Int, s: String)

  val bar: Bar = Bar(3,"three")

  val barJson: Json = bar.asJson

  println(s"Convert Bar object to Json with annotation \n ${barJson} \n")

  val barBack : Decoder.Result[Bar] = barJson.as[Bar]

  println(s"Convert Json to Bar object with annotation \n ${barBack.right.get} \n")

  /*
      forProductN helper methods
      construct encoders and decoders for case class-like types in a relatively boilerplate-free way without generic derivation
   */
  case class User(id: Long, firstName: String, lastName: String)

  implicit val decodeUser: Decoder[User] =
    Decoder.forProduct3("id", "first_name", "last_name")(User.apply)
  // decodeUser: io.circe.Decoder[User] = io.circe.ProductDecoders$$anon$3@7e6f0f9b

  implicit val encodeUser: Encoder[User] =
    Encoder.forProduct3("id", "first_name", "last_name")(u =>
      (u.id, u.firstName, u.lastName)
    )

  val user : User = User(1,"Baris","Atmn")

  val userJson: Json = user.asJson

  println(s"Convert User object to Json without generic derivation \n ${userJson} \n")

  val userBack : Decoder.Result[User] = userJson.as[User]

  println(s"Convert Json to User object without generic derivation \n ${userBack.right.get} \n")

  /*
  Circe uses shapeless to automatically derive the necessary type class instances
  import io.circe.generic.auto._
  */
  case class Person(name: String)
  // defined class Person

  case class Greeting(salutation: String, person: Person, exclamationMarks: Int)

  println(Greeting("Hey", Person("Chris"), 3).asJson)

  /*
   Custom codec
    */
  class Thing(val foo: String, val bar: Int)

  implicit val encodeFoo: Encoder[Thing] = new Encoder[Thing] {
    final def apply(a: Thing): Json = Json.obj(
      ("foo", Json.fromString(a.foo)),
      ("bar", Json.fromInt(a.bar))
    )
  }

  implicit val decodeFoo: Decoder[Thing] = new Decoder[Thing] {
    final def apply(c: HCursor): Decoder.Result[Thing] =
      for {
        foo <- c.downField("foo").as[String]
        bar <- c.downField("bar").as[Int]
      } yield {
        new Thing(foo, bar)
      }
  }

  /*
    Custom key types
    --> Note that K should not be String
    If you need to encode/decode Map[K, V] where K is not String (or Symbol, Int, Long, etc.), you need to provide a KeyEncoder and/or KeyDecoder for your custom key type.
  */

  case class FooKey(value: String)
  // defined class Foo

  implicit val fooKeyEncoder: KeyEncoder[FooKey] = new KeyEncoder[FooKey] {
    override def apply(foo: FooKey): String = foo.value
  }

  implicit val fooKeyDecoder: KeyDecoder[FooKey] = new KeyDecoder[FooKey] {
    override def apply(key: String): Option[FooKey] = Some(FooKey(key))
  }

  val map = Map[FooKey, Int](
    FooKey("hello") -> 123,
    FooKey("world") -> 456
  )

  val jsonMap:Json = map.asJson

  println(jsonMap)

  val jsonMapBack:Decoder.Result[Map[FooKey,Int]] = jsonMap.as[Map[FooKey, Int]]

  println(jsonMapBack)

}
