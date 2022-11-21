package com.nyx.ziotodo.services

import io.getquill.jdbczio.Quill
import io.getquill.*
import zio.*
import java.sql.SQLException

final case class AuthService(private val quill: Quill.Mysql[PluralizedTableNames]):
  import quill.*

  def auth(username: String, password: String): ZIO[Any, Throwable, Option[User]] =
    for {
      result <- run {
        quote { (username: String, password: String) =>
          for {
            user <- query[User] if username == user.username && password == user.password
          } yield user
        }(lift(username), lift(password))
      }.map(f => if f.isEmpty then None else Some(f.head))
    } yield result

  def register(username: String, password: String): ZIO[Any, SQLException, Long] =
    run {
      quote { (newUser: User) =>
        query[User].insertValue(newUser)
      }(lift(User(username = username, password = password)))
    }

  def deleteUser(id: Long): ZIO[Any, SQLException, Long] =
    run {
      quote { (id: Long) =>
        query[User].filter(_.id == id).delete
      }(lift(id))
    }

object AuthService:
  def service = ZIO
    .serviceWithZIO[AuthService]

  def auth(username: String, password: String): ZIO[AuthService, Throwable, Option[User]] =
    service(_.auth(username, password))

  def register(username: String, password: String): ZIO[AuthService, Throwable, Long] =
    service(_.register(username, password))

  def deleteUser(id: Long): ZIO[AuthService, SQLException, Long] =
    service(_.deleteUser(id))

  def live = ZLayer.fromFunction(AuthService(_))
