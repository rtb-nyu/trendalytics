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
    println("hello fb")

    val searchq = "kfc"

    val searchUrl = "/v2.8/search"
    val suri = new URIBuilder()
    .setScheme("https")
    .setHost("graph.facebook.com")
    .setPath(searchUrl)
    .setParameter("format", "json")
    .setParameter("access_token", "EAASmxuzxRbwBAPl4DbZAp1eeZBzjvrWv0B42IZA6RzAjxbmLk7mZAsjcB26hhGXC6ytFhwfJTVgbilp4PF7hdV7bOuLKV2RTsb2W8ZCZCl5rZBZBZAu1kgWqtW8I0K1E2taN6tMGle9oMnZByLfr8habVZBgf8uyhy7bMkZD")
    .setParameter("q",searchq)
    .setParameter("type","page")//.setParameter("limit", "25")
    .build();

    val httpclient = HttpClients.createDefault();

    val shttpget = new HttpGet(suri);

    val sresponse = httpclient.execute(shttpget);

   
      try {

            val sentity = sresponse.getEntity()
            
            if (sentity != null) {

                val sresponseString = EntityUtils.toString(sentity);
                //println(responseString)

                val sresponseJSON = parse(sresponseString)

                val sresults = sresponseJSON \ "data" \ "id"
                 val sls = for {
                     JObject(child) <- sresults
                     JField("message", JString(message)) <- child
                     JField("id", JString(id)) <- child
                     JField("created_time", JString(created_time)) <- child
                } yield (message, id, created_time)
         

               

                val pageId =  sls(0);
                val pageUrl  = "/v2.8/" + pageId + "/feed"
                   

                val uri = new URIBuilder()
                .setScheme("https")
                .setHost("graph.facebook.com")
                .setPath(pageUrl)
                .setParameter("format", "json")
                .setParameter("access_token", "EAASmxuzxRbwBAPl4DbZAp1eeZBzjvrWv0B42IZA6RzAjxbmLk7mZAsjcB26hhGXC6ytFhwfJTVgbilp4PF7hdV7bOuLKV2RTsb2W8ZCZCl5rZBZBZAu1kgWqtW8I0K1E2taN6tMGle9oMnZByLfr8habVZBgf8uyhy7bMkZD")
                //.setParameter("limit", "25")
                .build();

                //val httpclient = HttpClients.createDefault();

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
                        } yield (message, id, created_time)

                        val delimiter = "\t"



                        val pw = new PrintWriter(new File("facebook_posts/facebook.txt" ))

                        ls.foreach{ e => val (a, b, c) = e
                            
                            println("writing fb post data\n");

                            pw.write(a 
                                + delimiter + b 
                                + delimiter + c
                                + '\n')
                            
                            val cobjectId = b
                            val cpageUrl  = "/v2.8/" + cobjectId + "/comments"

                            val curi = new URIBuilder()
                            .setScheme("https")
                            .setHost("graph.facebook.com")
                            .setPath(cpageUrl)
                            .setParameter("format", "json")
                            .setParameter("access_token", "EAASmxuzxRbwBAPl4DbZAp1eeZBzjvrWv0B42IZA6RzAjxbmLk7mZAsjcB26hhGXC6ytFhwfJTVgbilp4PF7hdV7bOuLKV2RTsb2W8ZCZCl5rZBZBZAu1kgWqtW8I0K1E2taN6tMGle9oMnZByLfr8habVZBgf8uyhy7bMkZD")
                            .setParameter("limit", "10")
                            .build();

                            // val httpclient = HttpClients.createDefault();

                            val chttpget = new HttpGet(curi);

                            val cresponse = httpclient.execute(chttpget);
                            
                            try {

                                val centity = cresponse.getEntity()
                                
                                if (centity != null) {

                                    val cresponseString = EntityUtils.toString(centity);

                                    val cresponseJSON = parse(cresponseString)

                                    val cresults = cresponseJSON \ "data"

                                    val cls = for {
                                         JObject(child) <- cresults
                                         JField("message", JString(message)) <- child
                                         JField("id", JString(id)) <- child
                                         JField("created_time", JString(created_time)) <- child
                                       } yield (message, id, created_time)

                                    val delimiter = "\t"

                                    // val cfname = "facebook_posts/" + b + ".txt"

                                    // val cfw = new PrintWriter(new File(cfname))

                                    cls.foreach{ e => val (a, b, c) = e
                                        pw.write(a 
                                            + delimiter + b 
                                            + delimiter + c
                                            + '\n')
                                    }

                                    // cfw.close

                                    println("Finishd writing to comment");
                                }
                            } catch {
                                case e: Exception => println("exception caught: " + e);
                            } finally {
                                cresponse.close()
                            }
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
        } catch {
            case e: Exception => println("exception caught: " + e);
        } finally {
            sresponse.close()
        }
    }
}