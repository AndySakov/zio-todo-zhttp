package com.nyx.ziotodo

import zio.*
import com.nyx.ziotodo.services.*
import io.getquill.jdbczio.Quill
import io.getquill.*
import java.sql.SQLException
import com.nyx.ziotodo.services.AuthService
import java.sql.SQLIntegrityConstraintViolationException

object TodoApp extends ZIOAppDefault:
  val program: ZIO[TodoService & AuthService, Throwable, Unit] =
    for {
      todos <- TodoService.getUsersTodoLists(1)
      todo <- ZIO.succeed(todos)
      _ <- Console.printLine(todo)
      code <- AuthService.register("superadmin", "password").catchAll{
        ex => ex match
          case x: SQLIntegrityConstraintViolationException => ZIO.succeed(XResult(UserAlreadyExists("superadmin"), None))
          case x: Throwable => Console.printError(x.getMessage())
      }
      _ <- Console.printLine{
        code match
          case XResult(message, data) => message.value
          case x: Long => s"User created successfully: $code"
      }
      res <- AuthService.auth("superadmin", "password")
      _ <- Console.printLine(res)
    } yield ()

  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] =
    program
      .provide(
        Quill.DataSource.fromPrefix("db"),
        Quill.Mysql.fromNamingStrategy(PluralizedTableNames),
        TodoService.live,
        AuthService.live
      )
