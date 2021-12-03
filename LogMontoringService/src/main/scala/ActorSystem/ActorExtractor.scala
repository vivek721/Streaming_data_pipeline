package ActorSystem

import HelperUtils.CreateLogger
import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.KafkaProducer

import java.io.File
import java.nio.file.Files
import java.util.Properties
import scala.io.Source

class ActorExtractor extends Actor {

  // To print log messages in console
  val log = CreateLogger(classOf[ActorExtractor])

  // Get the config values from application.conf in resources
  val config = ConfigFactory.load("Application.conf")
  var lastReadLines: scala.collection.mutable.Map[String, Int] = scala.collection.mutable.Map[String, Int]()
  var lineCounter = 0
  log.info("Add property values for kafka producers")
  val props: Properties = new Properties()
  log.info("set kafka server")
  props.put("bootstrap.servers", config.getString("config.KafkaServer"))
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("acks", "all")
  val producer = new KafkaProducer[String, String](props)
  // Give the list of kafkaTopicNames here
  val kafkaTopicName = config.getString("kafkaTopicName")
//  producer.send(new ProducerRecord[String, String](kafkaTopicName,"kafkaProducer","kafkaProducer"))
  // To receive the file
  override def receive: Receive = {
    case file: File =>
      if (!lastReadLines.contains(file.getName)) lastReadLines += (file.getName -> 0)
      val BufferedSource = Source.fromFile(file)
      // Read the line from the file
      val data = Files.lines(file.toPath)
      data.skip(lastReadLines(file.getName)).forEach(SendTokafka(_))
      lastReadLines(file.getName) = BufferedSource.getLines.size
  }

  // To send data from producer to consumer
  def SendTokafka(data: String): Unit = {
    println(data)
  }

}
