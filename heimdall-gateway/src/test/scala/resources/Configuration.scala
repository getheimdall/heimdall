package resources

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.typesafe.config._

object Configuration {
	val baseURL = "http://localhost:8080"
	val acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
	val doNotTrackHeader = "1"
	val acceptLanguageHeader = "en-US,en;q=0.5"
	val acceptEncodingHeader = "gzip, deflate"
	val userAgentHeader = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0" 
	val httpConf = http.baseURL(baseURL)    
	.acceptHeader(acceptHeader)
	.doNotTrackHeader(doNotTrackHeader)
	.acceptLanguageHeader(acceptLanguageHeader)
	.acceptEncodingHeader(acceptEncodingHeader)
	.userAgentHeader(userAgentHeader);
}
