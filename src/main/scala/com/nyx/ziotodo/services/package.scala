package com.nyx.ziotodo

import java.sql.Date

package object services:
  case class Todo(id: Long = 1L, title: String, description: String, due_by: Option[Date], list_id: Long)
  case class TodoList(id: Long = 1L, name: String, user_id: Long)
  case class TodoListWithTodos(id: Long = 1L, name: String, user_id: Long, todos: List[Todo])
  case class User(id: Long = 1L, username: String, password: String)

  case class XResult[T](message: Message, data: T)

  trait Message(val value: String)

  case class UserAlreadyExists(username: String) extends Message(s"User with username `$username` already exists!")
  case class AccountCreatedSuccessfully() extends Message("Your account has been created successfully")
  case class UnknownError(message: String) extends Message(message)
