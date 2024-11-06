/** The operation to execute by a query engine.
 *
 * A query engine is a component responsible in managing the data storage in a
 * view to satisfy a SQL query. To do so, your request is Converted into an
 * execcution plan. An execution plan an organisation of a set of low level
 * operations to execute, in order to fulfill your request.
 *
 * To represent "SELECT * FROM my_table", you will write:
 * {{{
 * ExecutionPlan(TableScan("my_table", Projection(All, Nil, End)))
 * }}}
 *
 * "SELECT * FROM my_table WHERE count > 10"
 * {{{
 * ExecutionPlan(
 *   TableScan("my_table",
 *     Projection(All, Nil,
 *       Filter(Greater(Column("count"), LitInt(10)), Nil, End)
 *     )
 *   )
 * )
 * }}}
 *
 * "SELECT * FROM my_table RANGE 0, 10"
 * {{{
 * ExecutionPlan(
 *   TableScan("my_table",
 *     Projection(All, Nil,
 *       Range(0, 10, End)
 *     )
 *   )
 * )
 * }}}
 *
 * @param firstOperation
 *   the first operation to execute
 */
case class ExecutionPlan(firstOperation: Operation)

/** End of an execution plan. */
case object End extends Operation(None)

/** This is the representation of an operation to be executed by the request
 * engine.
 *
 * An Operation is an element of an execution plan. It can be a "table scan"
 * (ie. to read the rows in a table), a "projection" (ie. extracting and
 * transforming only the necessary columns), a "filter" (ie. removing some rows
 * according to criteria from the WHERE clause)...
 *
 * @param next
 *   the operation to execute after this one
 */
trait Operation(next: Option[Operation])

/** Read all the content of a table.
 *
 * @param tableName
 *   name of the table.
 * @param next
 */
case class TableScan(
                      tableName: String,
                      next: Option[Operation]
                    ) extends Operation(next)

/** Represent a column expression.
 */
enum ColumnExpression:
  /** Represent a column.
   *
   * @param name
   *   name of the column
   */
  case Column(name: String)

  /** integer value */
  case LitInt(value: Int)

  /** double value */
  case LitDouble(value: Double)

  /** string value */
  case LitString(value: String)

  /** Represent a function call
   *
   * @param function
   *   name of the function
   * @param col
   *   column sub-expression
   */
  case FunctionCall(function: String, col: ColumnExpression)

/** represent all available column (like in "SELECT *") */
case object All

/** Apply a projection and transformations to a rows.
 *
 * @param column
 *   first column expression to project on
 * @param otherColumns
 *   following column expression to project on if any
 * @param next
 */
case class Projection(
                       column: ColumnExpression | All.type,
                       otherColumns: List[ColumnExpression | All.type],
                       next: Option[Operation]
                     ) extends Operation(next)

/** Represent an expression in a filter (ie in WHERE clause). It can be
 */
enum FilterExpression:
  // c1 = c2
  case Equal(c1: ColumnExpression, c2: ColumnExpression)
  // c1 != c2
  case NotEqual(c1: ColumnExpression, c2: ColumnExpression)
  // c1 > c2
  case Greater(c1: ColumnExpression, c2: ColumnExpression)
  // c1 >= c2
  case GreaterOrEqual(c1: ColumnExpression, c2: ColumnExpression)
  // c1 < c2
  case Less(c1: ColumnExpression, c2: ColumnExpression)
  // c1 <= c2
  case LessOrEqual(c1: ColumnExpression, c2: ColumnExpression)

/** Filter rows according to criterion.
 *
 * All filter expressions are implicitly bounded by AND.
 *
 * @param filter
 *   first filter expression
 * @param filters
 *   following filter expression, if any
 * @param next
 */
case class Filter(
                   filter: FilterExpression,
                   filters: List[FilterExpression],
                   next: Option[Operation]
                 ) extends Operation(next)

/** Limit the number of lines to output.
 *
 * @param start
 *   line index to start with
 * @param count
 *   number of lines to output
 * @param next
 */
case class Range(
                  start: Int,
                  count: Int,
                  next: Option[Operation]
                ) extends Operation(next)