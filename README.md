# Basic Information

How to use Circe Library (Examples from circe.github.io)

## Circe cursors
- Cursor provides functionality for moving around a tree and making modifications
- HCursor tracks the history of operations performed. This can be used to provide useful error messages when something goes wrong.
- ACursor also tracks history, but represents the possibility of failure (e.g. calling downField on a field that doesn’t exist)

## Encoding and decoding
- circe uses Encoder and Decoder type classes for encoding and decoding. 
An Encoder[A] instance provides a function that will convert any A to a Json, 
and a Decoder[A] takes a Json value to either an exception or an A. 

- circe provides implicit instances of these type classes for many types from the Scala standard library, including Int, String, List[A], Option[A] and others
