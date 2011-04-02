package eu.appsatori.g2cl

import org.apache.http.client.ClientProtocolException;

import groovyx.net.http.RESTClient;

class GoogleClient extends RESTClient {

	Map<String, String> defaultQuery = [:]

	private GoogleClient(){

	}

	static GoogleClient getAnonymous(){
		GoogleClient google = []
		google.with {
			defaultRequestHeaders.'GData-Version' = 2.3
			defaultQuery.alt = 'jsonc'
		}
		google
	}
	
	static GoogleClient twoLegged(consumerKey, consumerSecret, requestor){
		GoogleClient google = anonymous
		google.with {
			auth.oauth consumerKey, consumerSecret, '', ''
			defaultQuery.xoauth_requestor_id = requestor
		}
		google
	}
	
	static GoogleClient threeLegged(consumerKey, consumerSecret, accessToken, secretToken){
		GoogleClient google = anonymous
		google.with {
			auth.oauth consumerKey, consumerSecret, accessToken, secretToken
		}
		google
	}

	def get(Map args) throws ClientProtocolException ,IOException ,URISyntaxException {
		addDefaultQuery args
		super.get(args)
	}

	def delete(Map args) throws URISyntaxException ,ClientProtocolException ,IOException {
		addDefaultQuery args
		super.delete(args)
	}

	def put(Map args) throws URISyntaxException ,ClientProtocolException ,IOException {
		addDefaultQuery args
		super.put(args)
	}

	def head(Map args) throws URISyntaxException ,ClientProtocolException ,IOException {
		addDefaultQuery args
		super.head(args)
	}

	def options(Map args) throws ClientProtocolException ,IOException ,URISyntaxException {
		addDefaultQuery args
		super.options(args)
	}

	def post(Map args) throws URISyntaxException ,ClientProtocolException ,IOException {
		addDefaultQuery args
		returnData super.post(args)
	}
	
	private addDefaultQuery(Map args) {
		if(args.query) {
			def newQuery = [:]
			newQuery << args.query
			newQuery << defaultQuery
			args.query = newQuery
		} else {
			args.query = new LinkedHashMap(defaultQuery)
		}
	}
	
}
