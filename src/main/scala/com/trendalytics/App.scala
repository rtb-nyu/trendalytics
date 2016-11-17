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

    val uri = new URIBuilder()
        .setScheme("https")
        .setHost("api.themoviedb.org")
        .setPath("/3/discover/movie")
        .setParameter("api_key", "492e30f6e746b7bfa8cef944042b5a64")
        .setParameter("sort_by", "popularity.desc")
        .build();

    val httpclient = HttpClients.createDefault();
    
    val httpget = new HttpGet(uri);
    
    val response = httpclient.execute(httpget);

    try {

        val entity = response.getEntity()
        
        if (entity != null) {

            val len = entity.getContentLength();

            val responseString = EntityUtils.toString(entity);

            val responseJSON = parse(responseString)

            val results = responseJSON \ "results"

            val ls = for {
                 JObject(child) <- results
                 JField("title", JString(title)) <- child
                 JField("release_date", JString(release_date)) <- child
                 JField("id", JInt(id)) <- child
                 JField("popularity", JDouble(popularity)) <- child
                 JField("vote_count", JInt(vote_count)) <- child
                 JField("vote_average", JDouble(vote_average)) <- child
                 JField("original_language", JString(original_language)) <- child
                 JField("adult", JBool(adult)) <- child
               } yield (title, release_date, id, popularity, vote_count, vote_average, original_language, adult)

            val delimiter = "\t"

            val pw = new PrintWriter(new File("movies.txt" ))

            ls.foreach{ e => val (a, b, c, d, f, g, h, i) = e
                pw.write(a 
                    + delimiter + b 
                    + delimiter + c.toString 
                    + delimiter + d.toString 
                    + delimiter + f.toString 
                    + delimiter + g.toString 
                    + delimiter + h
                    + delimiter + i.toString + '\n')
            }

            pw.close

            println("Finished Writing Data to file");

        }
    } catch {
        case e: Exception => println("exception caught: " + e);
    } finally {
        response.close()
    } 
  }

}
