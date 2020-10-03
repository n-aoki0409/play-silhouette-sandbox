package generator

import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator

object SlickCodeGenerator extends App {
  val config = ConfigFactory.load()

  val slickDriver  = "slick.jdbc.MySQLProfile"
  val jdbcDriver   = config.getString("slick.dbs.default.db.driver")
  val url          = config.getString("slick.dbs.default.db.url")
  val outputFolder = "app"
  val pkg          = "infrastructure.repositories"
  val user         = config.getString("slick.dbs.default.db.user")
  val password     = config.getString("slick.dbs.default.db.password")

  SourceCodeGenerator.run(
    slickDriver,
    jdbcDriver,
    url,
    outputFolder,
    pkg,
    Some(user),
    Some(password),
    true,
    false
  )
}
