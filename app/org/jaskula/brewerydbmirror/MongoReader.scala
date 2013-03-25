package org.jaskula.brewerydbmirror

import com.google.inject.Inject
import com.mongodb.DBObject
import com.google.inject.Singleton

@Singleton
class MongoReader @Inject()(mongo: MongoService){
  
  def allBeers(): Iterator[DBObject] = {
    mongo.beers.find()
  }
}