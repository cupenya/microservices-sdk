package com.github.cupenya.microservices.sdk.dao

import java.util.UUID

import org.mongodb.scala.{MongoClient, MongoDatabase}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import spray.json._

class MongoDaoTest extends FlatSpec with Matchers with DefaultJsonProtocol with ScalaFutures with MongoContext {

  implicit val testFormat = jsonFormat2(Test)

  implicit val idFormat = jsonFormat2(Id)
  implicit val test2Format = jsonFormat2(Test2)

  case class Test(id: String, foo: Double)

  object TestMongoDao extends MongoDao[String, Test] {
    override protected def collectionName: String = "tests"
  }

  case class Id(id: String, timestamp: Long)

  case class Test2(id: Id, foo: Double)

  object Test2MongoDao extends MongoDao[Id, Test2] {
    override protected def collectionName: String = "test2s"
  }

  "MongoDao" should "insert test record" in new DaoDefaultContext {
    private val uuid = UUID.randomUUID()
    private val test: Test = Test(uuid.toString, 0d)

    whenReady(TestMongoDao.findOne(uuid.toString)) { result =>
      result shouldBe None
    }

    TestMongoDao.insert(test)

    whenReady(TestMongoDao.findOne(uuid.toString)) { result =>
      result shouldBe Some(test)
    }
  }
}

trait MongoContext extends Suite with BeforeAndAfterAll {
  val contextId = UUID.randomUUID().toString

  val mongoHost = "localhost"
  val mongoPort = 27018
  val mongoUri = s"mongodb://$mongoHost:$mongoPort"
  val testDbName = MongoContext.DB_NAME_PREFIX + (Math.random() * 100000).toInt

  private var _mongoClient: MongoClient = _

  def mongoClient: MongoClient = _mongoClient
//
  def getDb: MongoDatabase = _mongoClient.getDatabase(testDbName)

  override def beforeAll(): Unit = {
    _mongoClient =  MongoClient(mongoUri)
  }

  override def afterAll() {
    if (_mongoClient != null) {
      getDb.drop().map(_ => _mongoClient.close())
    }
  }
}

object MongoContext {
  val DB_NAME_PREFIX = "unittest"
}

protected[this] trait DaoDefaultContext extends MongoContext {
  implicit val db: MongoDatabase = getDb
}
