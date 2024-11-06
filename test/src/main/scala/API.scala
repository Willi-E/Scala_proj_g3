case class API()



  type TableName = String
  type ColumnName = String
  type RowValue = Any // value of any type

  // SCHEMA

case class Schema(key: ColumnDeclaration, columns: List[ColumnDeclaration])

case class ColumnDeclaration(name: ColumnName, colType: ColumnType)

enum ColumnType(val name: String):
    case IntType extends ColumnType("INTEGER")
    case StringType extends ColumnType("STRING")
    case BooleanType extends ColumnType("BOOLEAN")
    case TimestampType extends ColumnType("TIMESTAMP")

object ColumnType:
    val byName = ColumnType.values.map(t => (t.name, t)).toMap

    def from(name: String): Option[ColumnType] = byName.get(name)

  // RESULT

  /** Result of an execution plan */
case class Result(
                     /** available columns in the result */
                     columns: List[ColumnName],

                     /** rows of the result with their respective values */
                     rows: List[Row]
                   )

case class Row(data: Map[ColumnName, RowValue]):
    def get(columnName: ColumnName): Option[RowValue]

trait Database:
  def openOrCreate(tableName: TableName, schema: Schema): Either[DatabaseError, Table]

  def drop(tableName: TableName): Either[DatabaseError, Unit]

trait Table(tableName: TableName, schema: Schema, store: Store):
  def insert(values: Map[ColumnName, Any]): Either[DatabaseError, Unit]

  def execute(executionPlan: ExecutionPlan): Either[DatbaseError, Result]