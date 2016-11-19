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

class FacebookStreamer {

  def fetch() {

    val pageId = "15925638948"
    val pageUrl  = "/v2.8/" + pageId + "/feed"

    val uri = new URIBuilder()
    .setScheme("https")
    .setHost("graph.facebook.com")
    .setPath(pageUrl)
    .setParameter("format", "json")
    .setParameter("access_token", "EAABvHGgFBoUBAMSg8ZBelgeZCATDXrS1jHkZAgxCg2qNKkZAwsHJysNPEBz58vYVG2YHxewocV1SAWKEK1exvbGyh2JEHMtJy0sZCOShykYrG9KqwFtMteiSVKXJFrRZBK6e2DaegPlUwko34nJSXD")
    .setParameter("limit", "25")
    .setParameter("until", "1479183350")
    .build();

    val httpclient = HttpClients.createDefault();

    val httpget = new HttpGet(uri);

    val response = httpclient.execute(httpget);

    try {

        val entity = response.getEntity()
        
        if (entity != null) {

            val responseString = EntityUtils.toString(entity);
            println(responseString)

            val responseJSON = parse(responseString)

            val results = responseJSON \ "data"

            val ls = for {
                 JObject(child) <- results
                 JField("message", JString(message)) <- child
                 JField("id", JString(id)) <- child
                 JField("created_time", JString(created_time)) <- child
        //          JField("popularity", JDouble(popularity)) <- child
        //          JField("vote_count", JInt(vote_count)) <- child
        //          JField("vote_average", JDouble(vote_average)) <- child
        //          JField("original_language", JString(original_language)) <- child
        //          JField("adult", JBool(adult)) <- child
               } yield (message, id, created_time)

            val delimiter = "\t"

            val pw = new PrintWriter(new File("facebook.txt" ))

            ls.foreach{ e => val (a, b, c) = e
                pw.write(a 
                    + delimiter + b 
                    + delimiter + c
                    + '\n')
            }

            pw.close

            println("Finished Writing FB Data to file");

        }
    } catch {
        case e: Exception => println("exception caught: " + e);
    } finally {
        response.close()
    }


  }
}
