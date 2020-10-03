package infrastructure.repositories
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Users.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Users
   *  @param userId Database column USER_ID SqlType(CHAR), PrimaryKey, Length(20,false)
   *  @param firstName Database column FIRST_NAME SqlType(VARCHAR), Length(100,true)
   *  @param lastName Database column LAST_NAME SqlType(VARCHAR), Length(100,true)
   *  @param email Database column EMAIL SqlType(VARCHAR), Length(100,true)
   *  @param hashedPassword Database column HASHED_PASSWORD SqlType(VARCHAR), Length(256,true)
   *  @param createdAt Database column CREATED_AT SqlType(TIMESTAMP), Default(None)
   *  @param updatedAt Database column UPDATED_AT SqlType(TIMESTAMP), Default(None) */
  case class UsersRow(userId: String, firstName: String, lastName: String, email: String, hashedPassword: String, createdAt: Option[java.sql.Timestamp] = None, updatedAt: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[String], e1: GR[Option[java.sql.Timestamp]]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, Some("silhouette_db"), "users") {
    def * = (userId, firstName, lastName, email, hashedPassword, createdAt, updatedAt) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(userId), Rep.Some(firstName), Rep.Some(lastName), Rep.Some(email), Rep.Some(hashedPassword), createdAt, updatedAt)).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column USER_ID SqlType(CHAR), PrimaryKey, Length(20,false) */
    val userId: Rep[String] = column[String]("USER_ID", O.PrimaryKey, O.Length(20,varying=false))
    /** Database column FIRST_NAME SqlType(VARCHAR), Length(100,true) */
    val firstName: Rep[String] = column[String]("FIRST_NAME", O.Length(100,varying=true))
    /** Database column LAST_NAME SqlType(VARCHAR), Length(100,true) */
    val lastName: Rep[String] = column[String]("LAST_NAME", O.Length(100,varying=true))
    /** Database column EMAIL SqlType(VARCHAR), Length(100,true) */
    val email: Rep[String] = column[String]("EMAIL", O.Length(100,varying=true))
    /** Database column HASHED_PASSWORD SqlType(VARCHAR), Length(256,true) */
    val hashedPassword: Rep[String] = column[String]("HASHED_PASSWORD", O.Length(256,varying=true))
    /** Database column CREATED_AT SqlType(TIMESTAMP), Default(None) */
    val createdAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("CREATED_AT", O.Default(None))
    /** Database column UPDATED_AT SqlType(TIMESTAMP), Default(None) */
    val updatedAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("UPDATED_AT", O.Default(None))

    /** Uniqueness Index over (email) (database name EMAIL) */
    val index1 = index("EMAIL", email, unique=true)
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}
