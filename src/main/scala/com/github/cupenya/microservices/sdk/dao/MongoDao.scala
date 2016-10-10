package com.github.cupenya.microservices.sdk.dao

import org.mongodb.scala._
import spray.json._

import scala.concurrent.Future
import spray.json.DefaultJsonProtocol.StringJsonFormat
import scala.language.implicitConversions

abstract class SimpleMongoDao[T : JsonFormat] extends MongoDao[String, T] {
  implicit val stringJsonFormat = StringJsonFormat // this is to make sure the import is used
}

abstract class MongoDao[ID : JsonFormat, T : JsonFormat] {
  protected def collectionName: String

  private def collection(implicit db: MongoDatabase) = db.getCollection(collectionName)

  private def docToT(doc: Document): T =
    doc.toJson.parseJson.convertTo[T]

  private def eToDoc[E : JsonFormat](e: E): Document =
    Document(e.toJson.toString)

  implicit def tToDoc(t: T): Document = eToDoc(t)

  implicit def idToDoc(id: ID): Document = eToDoc(id)

  def list(doc: Document)(implicit db: MongoDatabase): Future[Seq[T]] =
    collection.find(doc).map(docToT).toFuture()

  def findOne(id: ID)(implicit db: MongoDatabase): Future[Option[T]] =
    findOne(Document("_id" -> idToDoc(id)))

  def findOne(doc: Document)(implicit db: MongoDatabase): Future[Option[T]] =
    collection.find(doc).map(doc => Option(doc).map(docToT)).head()

  def removeByField(doc: Document)(implicit db: MongoDatabase): Future[Option[T]] =
    collection.findOneAndDelete(doc).map(doc => Option(doc).map(docToT)).head()

  def insert(doc: Document)(implicit db: MongoDatabase): Future[Unit] =
    collection.insertOne(doc).map(_ => {}).head()

  def insert(t: T)(implicit db: MongoDatabase): Future[Unit] =
    collection.insertOne(t).map(_ => {}).head()
}
