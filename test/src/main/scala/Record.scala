import java.time.LocalDateTime

// define type aliases.
// All keys and values are of type String
type Key = String
type Value = String

case class Record(
                   key: Key,
                   value: Value,
                   /** timestamp the entry has been recorded in the store */
                   timestamp: LocalDateTime,
                   /** indicate if the entry has been deleted */
                   deleted: Boolean
                 )

enum StoreError:
  case KeyNotFound(key: Key)

trait Store:
  /** get the value associated to key */
  def get(key: Key): Either[StoreError, Value]
  /** add a key-value entry in the store */
  def put(key: Key, value: Value): Either[StoreError, Unit]
  /** remove a key from the store */
  def delete(key: Key): Either[StoreError, Unit]
  /** get an iterator over all entries from the store */
  def scan(): Either[StoreError, Iterator[Record]]
  /** get an iterator over entries starting from a given key */
  def getFrom(key: Key): Either[StoreError, Iterator[Record]]
  /** get an iterator over the entries which key starts with the same prefix */
  def getPrefix(prefix: String): Either[StoreError, Iterator[Record]]