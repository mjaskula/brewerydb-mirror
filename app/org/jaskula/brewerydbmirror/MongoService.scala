package org.jaskula.brewerydbmirror

import javax.inject.Inject
import play.api.Configuration
import com.mongodb.casbah.MongoClient
import javax.inject.Singleton
import com.mongodb.casbah.MongoDB
import com.mongodb.casbah.MongoCollection

@Singleton
class MongoService @Inject() (config: Configuration) {

  private val db: MongoDB =  MongoClient()(config.getString("mongodb.default.db").getOrElse("test"))
  
  // breweryDb mirror collections
  val styles:MongoCollection = db("styles")
  val breweries:MongoCollection = db("breweries")
  val beers:MongoCollection = db("beers")

  // meta-data collections
  val apiCalls:MongoCollection = db("apiCalls")
  val commandLog:MongoCollection = db("commandLog")
}