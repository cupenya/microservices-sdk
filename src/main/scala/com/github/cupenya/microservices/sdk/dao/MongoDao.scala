package com.github.cupenya.microservices.sdk.dao

import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}

trait MongoDao[T <: Product] {
  protected def collectionName: String
  def eventualCollection(db: Future[DefaultDB])(implicit ec: ExecutionContext): Future[BSONCollection] =
    db.map(_(collectionName))

  implicit def reader: BSONDocumentReader[T] // Macros.reader[YourCaseClass] to implement
  implicit def writer: BSONDocumentWriter[T] // Macros.writer[YourCaseClass] to implement

  implicit class RichBsonCollection(collection: BSONCollection) {
    def list(doc: BSONDocument)(implicit ec: ExecutionContext): Future[List[T]] =
      collection.find(doc).cursor[T]().collect[List]()

//    def findOne(id: ID): Future[Option[T]] =
//      findOne(document("_id" -> id))

    def findOne(doc: BSONDocument)(implicit ec: ExecutionContext): Future[Option[T]] =
      list(doc).map(_.headOption)

    def removeByField(doc: BSONDocument)(implicit ec: ExecutionContext): Future[Option[T]] =
      collection.findAndRemove(doc).map(_.result)

    def insert(doc: BSONDocument)(implicit ec: ExecutionContext): Future[Boolean] =
      collection.insert(doc).map(_.ok)

    def insert(t: T)(implicit ec: ExecutionContext): Future[Boolean] =
      collection.insert(t).map(_.ok)
  }
}
