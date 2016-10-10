package com.github.cupenya.microservices.sdk.dao

import org.scalatest.FunSuite
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}

class MongoDaoTest extends FunSuite {

  case class Test(id: String, foo: Double)
  class TestMongoDao extends SimpleMongoDao[Test] {
    override protected def collectionName: String = "tests"

    override implicit def entityReader: BSONDocumentReader[Test] = Macros.reader[Test]

    // Macros.reader[YourCaseClass] to implement
    override implicit def entityWriter: BSONDocumentWriter[Test] = Macros.writer[Test]
  }

  case class Id(id: String, timestamp: Long)

  case class Test2(id: Id, foo: Double)

  class Test2MongoDao extends MongoDao[Id, Test2] {
    override protected def collectionName: String = "test2s"

    override implicit def entityReader: BSONDocumentReader[Test2] = Macros.reader[Test2]

    // Macros.reader[YourCaseClass] to implement
    override implicit def entityWriter: BSONDocumentWriter[Test2] = Macros.writer[Test2]

    override implicit def idReader: BSONDocumentReader[Id] = Macros.reader[Id]

    // Macros.reader[YourCaseClass] to implement
    override implicit def idWriter: BSONDocumentWriter[Id] = Macros.writer[Id]
  }
}
