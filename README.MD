# ZIO TO-DO app scaffolding

This is a template ZIO application for building a TO-DO list app backend.

## Features

- CRUD ops for TODO's
- CRUD ops for Users
- Flyway Migrations for initial project setup

## Running

To run this project

- Clone the repo
- Run `cp .env.example .env`
- Populate the `.env` file with the appropriate infoAd
- Run `docker-compose up -d`
- Run `sbt flywayMigrate`
- Run `sbt r`
