package splice.v1

import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import splice.BaseSpec

class SpliceDataSourceV1Spec extends BaseSpec {

  "Splice Machine Connector (Data Source API V1)" should "support batch reading" in
    withSparkSession { spark =>
      val schema = StructType(Seq(
        StructField("id", LongType),
        StructField("name", StringType)
      ))
      val q = spark
        .read
        .format(SpliceDataSourceV1.NAME)
        .schema(schema)
        .option(SpliceOptions.JDBC_URL, "jdbc:splice://<jdbcUrlString>")
        .load
      val leaves = q.queryExecution.logical.collectLeaves()
      leaves should have length 1

      import org.apache.spark.sql.execution.datasources.LogicalRelation
      leaves
        .head
        .asInstanceOf[LogicalRelation]
        .relation
        .asInstanceOf[SpliceRelation]
    }

  it should "throw an IllegalStateException when required options (e.g. url) are not defined" in
    withSparkSession { spark =>
      an [IllegalStateException] should be thrownBy {
        spark
          .read
          .format(SpliceDataSourceV1.NAME)
          .load
      }
    }

  it should "support streaming write (with extra session-scoped options)" in
    withSparkSession { spark =>

      // FIXME Make sure that the options are passed on
      spark.conf.set("spark.datasource.splice.session.option", "session-value")

      import java.util.UUID

      import org.apache.spark.sql.streaming.Trigger

      import concurrent.duration._
      val sq = spark
        .readStream
        .format("rate")
        .load
        .writeStream
        .format(SpliceDataSourceV1.NAME)
        .option("splice.option", "option-value")
        .option("checkpointLocation", s"target/checkpointLocation-${UUID.randomUUID()}")
        .trigger(Trigger.ProcessingTime(1.second))
        .start()

      sq should be('active)

      // FIXME Let the streaming query execute twice or three times exactly
      import concurrent.duration._
      sq.awaitTermination(2.seconds.toMillis)
      sq.stop()

      sq should not be 'active

      val progress = sq.lastProgress
      val actual = progress.sink.description
      val expected = s"splice.v1.SpliceSink[${SpliceDataSourceV1.NAME}]"
      actual should be(expected)
    }
}
