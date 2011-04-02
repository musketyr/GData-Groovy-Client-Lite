package eu.appsatori.g2cl

import groovy.sql.Sql
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

class PrivateSandboxSpec extends Specification {	
	
	@Shared sql = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")
	
	// normally an external database would be used,
	// and the test data wouldn't have to be inserted here
	def setupSpec() {
	  sql.execute("create table credentials (id int primary key, consumerKey varchar(50), consumerSecret varchar(50), accessToken varchar(50), secretToken varchar(50), requestor varchar(50))")
	  sql.execute(new File("/home/ladin/credentials.sql").text)
	}
	
	def "Three legged"(){
		setup:
		def google = GoogleClient.threeLegged(consumerKey, consumerSecret, accessToken, secretToken)
		
		when:
		def calendars = google.get(uri: "https://www.google.com/calendar/feeds/default/")
		
		then:
		calendars
		calendars.status == 200
		
		where:
		[consumerKey, consumerSecret, accessToken, secretToken] << sql.rows("select consumerKey, consumerSecret, accessToken, secretToken from credentials where accessToken is not null and accessToken not like ''")
	}
	
	def "2-legged OAuth"(){
		setup:
		def google = GoogleClient.twoLegged(consumerKey, consumerSecret, requestor)
		
		when:
		def calendars = google.get(uri: "https://www.google.com/calendar/feeds/default/")
		
		then:
		calendars
		calendars.status == 200
		
		where:
		[consumerKey, consumerSecret, requestor] << sql.rows("select consumerKey, consumerSecret, requestor from credentials where accessToken is null or accessToken like ''")
	}
	
}
