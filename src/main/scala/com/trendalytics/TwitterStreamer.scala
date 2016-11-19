package com.trendalytics

import java.io._
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.native.JsonMethods._
import twitter4j._

/**
 * @author ${user.name}
 */
object Util {
  val config = new twitter4j.conf.ConfigurationBuilder()
  .setOAuthConsumerKey("vW4u1UHOr7T9LloRXbGI9A")
  .setOAuthConsumerSecret("Wt1E87vMHENhe54EejfhYNKZwbnscz1iMMVQUdRQ")
  .setOAuthAccessToken("1017369080-iAuxzsupRXi7SSbJqKqbT4LewpaKixxPgwt5j8o")
  .setOAuthAccessTokenSecret("CpU6Vmazvz9lDmAr8cLobTr3zzU0c1upNmAhDBWXDSt9B")
  .build

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) { println(status.getText) }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }
}

class TwitterStreamer {
  def fetch() {
    val twitterStream = new TwitterStreamFactory(Util.config).getInstance
    twitterStream.addListener(Util.simpleStatusListener)
    twitterStream.sample
    Thread.sleep(2000)
    twitterStream.cleanUp
    twitterStream.shutdown
  }
}
