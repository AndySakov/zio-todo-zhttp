package com.nyx.ziotodo.services

import io.getquill.jdbczio.Quill
import io.getquill.*
import zio.*
import java.sql.SQLException

final case class TodoService(private val quill: Quill.Mysql[PluralizedTableNames]):
  import quill.*

  def getUsersTodoLists(user_id: Long): ZIO[Any, SQLException, List[(TodoList, Todo)]] = run(
    quote { (user_id: Long) =>
      query[TodoList]
        .filter(_.user_id == user_id)
        .join(query[Todo])
        .on(_.id == _.list_id)
    }(lift(user_id))
  )

  def getTodosByList(list_id: Long): ZIO[Any, SQLException, List[Todo]] = run {
    quote { (list_id: Long) =>
      query[Todo].filter(_.list_id == list_id)
    }(lift(list_id))
  }

  def addTodo(todo: Todo): ZIO[Any, SQLException, Long] = run {
    quote {
      (todo: Todo) => query[Todo].insertValue(todo)
    }(lift(todo))
  }

  def updateTodo(todo: Todo): ZIO[Any, SQLException, Long] = run {
      quote {
        (todo: Todo) => query[Todo].filter(_.id == todo.id).updateValue(todo)
      }(lift(todo))
    }

  def deleteTodo(id: Long): ZIO[Any, SQLException, Long] = run {
        quote {
          (id: Long) => query[Todo].filter(_.id == id).delete
        }(lift(id))
      }

object TodoService:
  def service = ZIO
      .serviceWithZIO[TodoService]
  def getUsersTodoLists(user_id: Long): ZIO[TodoService, SQLException, List[TodoListWithTodos]] =
    service(_.getUsersTodoLists(user_id))
      .map(
        _.groupBy(_._1)
          .map {
            case (x, y) =>
              TodoListWithTodos(id = x.id, name = x.name, user_id = x.user_id, todos = y.map(_._2))
          }
          .toList
      )

  def getTodosByList(list_id: Long): ZIO[TodoService, SQLException, List[Todo]] =
    service(_.getTodosByList(list_id))

  def addTodo(todo: Todo): ZIO[TodoService, SQLException, Long] =
    service(_.addTodo(todo))

  def updateTodo(todo: Todo): ZIO[TodoService, SQLException, Long] =
      service(_.updateTodo(todo))

  def deleteTodo(id: Long): ZIO[TodoService, SQLException, Long] =
      service(_.deleteTodo(id))

  def live = ZLayer.fromFunction(TodoService(_))
