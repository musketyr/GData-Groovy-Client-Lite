package eu.appsatori.g2cl

import groovy.sql.Sql;
import groovyx.net.http.HTTPBuilder;
import spock.lang.Specification;
import spock.lang.Shared;

class PrivateSandboxSpec extends Specification {	
	
	@Shared sql = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")
	
	// normally an external database would be used,
	// and the test data wouldn't have to be inserted here
	def setupSpec() {
	  sql.execute("create table credentials (id int primary key, consumerKey varchar(50), consumerSecret varchar(50), accessToken varchar(50), secretToken varchar(50))")
	  sql.execute(new File("src/test/resources/private/credentials.sql").text)
	}
	
	def "Authorize properly"(){
		setup:
		def http = new HTTPBuilder("https://www.google.com/calendar/feeds/default/allcalendars/full")
		http.auth.oauth consumerKey, consumerSecret, accessToken, secretToken
		http.headers.'GData-Version' = 2
		def items = []
		when:
		http.get(query: [alt: 'jsonc']){ resp, json ->
			items = json.data.items
			items.each { println it }
	    }
		
		then:
		items
		
		where:
		[consumerKey, consumerSecret, accessToken, secretToken] << sql.rows('select consumerKey, consumerSecret, accessToken, secretToken from credentials')
	}
	
}
