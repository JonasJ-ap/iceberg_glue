import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

import org.apache.iceberg.Table
import org.apache.iceberg.aws.glue.GlueCatalog
import org.apache.iceberg.catalog.Catalog
import org.apache.iceberg.aws.AssumeRoleAwsClientFactory
import org.apache.iceberg.catalog.TableIdentifier
import org.apache.iceberg.spark.actions.SparkActions

import scala.jdk.CollectionConverters._

object GlueApp {
  def main(sysArgs: Array[String]): Unit = {
    val sparkContext: SparkContext = new SparkContext()
    val spark: SparkSession = SparkSession.builder
      .config("spark.sql.catalog.demo", "org.apache.iceberg.spark.SparkCatalog")
      .config(
        "spark.sql.catalog.demo.warehouse",
        "s3://gluetestjonas/warehouse"
      )
      .config(
        "spark.sql.catalog.demo.catalog-impl",
        "org.apache.iceberg.aws.glue.GlueCatalog"
      )
      .config(
        "spark.sql.catalog.demo.client.factory",
        "org.apache.iceberg.aws.AssumeRoleAwsClientFactory"
      )
      .config(
        "spark.sql.catalog.demo.client.assume-role.arn",
        "arn:aws:iam::481640105715:role/jonasjiang_gluecatalog"
      )
      .config("spark.sql.catalog.demo.client.assume-role.region", "us-east-1")
      .config(
        "spark.sql.catalog.demo.client.assume-role.session-name",
        "testApache"
      )
      .config(
        "spark.sql.catalog.demo.client.assume-role.external-id",
        "1234546"
      )
      .config("spark.sql.catalog.demo.http-client.type", "apache")
      .config(
        "spark.sql.catalog.demo.http-client.apache.connection-timeout-ms",
        "10000"
      )
      .config(
        "spark.sql.catalog.demo.http-client.apache.socket-timeout-ms",
        "5000"
      )
      .config(
        "spark.sql.catalog.demo.http-client.apache.connection-acquisition-timeout-ms",
        "5010"
      )
      .config(
        "spark.sql.catalog.demo.http-client.apache.connection-max-idle-time-ms",
        "5001"
      )
      .config(
        "spark.sql.catalog.demo.http-client.apache.connection-time-to-live-ms",
        "5002"
      )
      .config(
        "spark.sql.catalog.demo.http-client.apache.expect-continue-enabled",
        "true"
      )
      .config("spark.sql.catalog.demo.http-client.apache.max-connections", "10")
      .config(
        "spark.sql.catalog.demo.http-client.apache.tcp-keep-alive-enabled",
        "true"
      )
      .config(
        "spark.sql.catalog.demo.http-client.apache.use-idle-connection-reaper-enabled",
        "false"
      )
      .getOrCreate()

    spark.sql("CREATE DATABASE IF NOT EXISTS demo.reviewsjonas")

    val book_reviews_location =
      "s3://amazon-reviews-pds/parquet/product_category=Books/*.parquet"
    val book_reviews = spark.read.parquet(book_reviews_location)
    book_reviews
      .writeTo("demo.reviewsjonas.book_reviews_apache_http")
      .tableProperty("format-version", "2")
      .createOrReplace()

    // read using SQL
    // spark.sql("SELECT * FROM demo.reviews.book_reviews").show()
  }

}
