import org.apache.log4j._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}


object Hero {
    case class Hero(value: String)
    case class HeroName(id: Int, name: String)

    def main(args: Array[String]) {
        val spark = SparkSession.builder.appName("Hero").master("local[*]").getOrCreate()

        val heroNameSchema = new StructType()
            .add("id", IntegerType, nullable=true)
            .add("name", StringType, nullable=true)

        import spark.implicits._
        val heroNameData = spark.read
            .schema(heroNameSchema)
            .option("sep"," ")
            .csv("data/Marvel-names.txt")
            .as[HeroName]
        

        //transform heroNameData to map
        val heroNameMap: Map[Int, String] = heroNameData.rdd.map(hero => (hero.id, hero.name)).collectAsMap().toMap

        // broadcast heroNameData
        val broadcastHeroNameMap = spark.sparkContext.broadcast(heroNameMap)

        val heroData = spark.read
            .text("data/Marvel-graph.txt")
            .as[Hero]
        
        val heroAppearance = heroData
            .withColumn("id", split(col("value"), " ")(0).cast(IntegerType))
            .withColumn("appearances", size(split(col("value"), " ")) - 1)
        
        val aggHeroAppearance = heroAppearance.groupBy(col("id")).agg(sum(col("appearances")).alias("appearances")).sort(col("appearances").desc)

        val getHeroName: Int => String =(heroId:Int) => {
            broadcastHeroNameMap.value(heroId)
        }

        val getHeroNameUdf = udf(getHeroName)
        
        val aggHeroAppearanceWithName = aggHeroAppearance.withColumn("name", getHeroNameUdf(col("id")))
        
        aggHeroAppearanceWithName.show()

        spark.stop()
    }

}