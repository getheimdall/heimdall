package resources

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RatelimitSimulation extends Simulation {

	val scn = scenario("Routing")
	.exec(http("Foward")
	.get("/pier/v2/api/contas")
	.headers(Headers.securityHeader))

	setUp(scn.inject(atOnceUsers(80)).protocols(Configuration.httpConf))
	//setUp(scn.inject(constantUsersPerSec(80) during(50 seconds)).protocols(Configuration.httpConf))
}
