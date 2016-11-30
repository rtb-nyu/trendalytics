package com.trendalytics

import java.io._
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.native.JsonMethods._
/**
 * @author ${user.name}
 */
object App {
  
  def main(args : Array[String]) {

    // val twitter = new TwitterStreamer()

    // twitter.fetch()

     val facebook = new FacebookStreamer()
    facebook.fetch()

    // val tmdb = new TMDBStreamer()
    // tmdb.fetch()

    //val yelp = new YelpStreamer()
    //yelp.fetch()

    return

  }
}
