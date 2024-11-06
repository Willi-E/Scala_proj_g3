import scala.collection.mutable.TreeMap

class MemoryStore extends Store:
  // note: TreeMap is a kind of Map where data are sorted according to the key
  private val data: TreeMap[Key, Value] = TreeMap.empty 

  override def get(key: Key): Either[StoreError, Value] =
    data.get(key).toRight(StoreError.KeyNotFound(key))

  override def put(key: Key, value: Value): Either[StoreError, Unit] = Right {
    data.update(key, value); ()
  }

  override def delete(key: Key): Either[StoreError, Unit] =
    data
      .get(key)
      .toRight(StoreError.KeyNotFound(key))
      .map(r => data.update(key, r.copy(deleted = true)))

  override def scan(): Either[StoreError, Iterator[Record]] = Right {
    data.iterator
  }

  override def getFrom(key: Key): Either[StoreError, Iterator[Record]] = Right {
    data.iteratorFrom(key).map(_._2)
  }

  override def getPrefix(prefix: String): Either[StoreError, Iterator[Record]] =
    Right { data.filter((k, _) => k.startsWith(prefix)).iterator }