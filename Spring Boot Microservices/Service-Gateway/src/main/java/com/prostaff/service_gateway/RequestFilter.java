package com.prostaff.service_gateway;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.client.RestTemplate;
import com.prostaff.service_gateway.dto.AuthRequest;

@SuppressWarnings("rawtypes")
public class RequestFilter implements GatewayFilterFactory{
	 
		private static List<String> openForAllPaths;
		private static List<String> accessibleOrganizationMethods;
		
		static {
			openForAllPaths = new ArrayList<>();
			accessibleOrganizationMethods = new ArrayList<>();
			
			openForAllPaths.add("/auth/login");
			openForAllPaths.add("/auth/send-reset-password-mail");
			openForAllPaths.add("/auth/request-reset-password");
			openForAllPaths.add("/auth/create-order");
			openForAllPaths.add("/auth/verify-payment");
			openForAllPaths.add("/auth/verify-new-organization-details");
			
			
			accessibleOrganizationMethods.add("/organization/get-organization-data");
			accessibleOrganizationMethods.add("/organization/get-faqs");
			accessibleOrganizationMethods.add("/organization/get-upcoming-events");
			accessibleOrganizationMethods.add("/organization/get-help-details");
			accessibleOrganizationMethods.add("/organization/update-help-details");
			accessibleOrganizationMethods.add("/organization/add-upcoming-event");
			accessibleOrganizationMethods.add("/organization/add-faq");
			accessibleOrganizationMethods.add("/organization/update-upcoming-event");
			accessibleOrganizationMethods.add("/organization/update-faq");
		}
		
		@Autowired
		private RestTemplate restTemplate;
		
		@Override
		  public GatewayFilter apply(Object config) {
		    return (exchange, chain) -> {
		      ServerHttpRequest request = exchange.getRequest();
		      
		      String path = request.getPath().toString();
		      
		      
		      // SWAGGER
		      if(path.contains("/swagger-ui/") || path.contains("/v3/api-docs"))
		      {
		    	  String val = "aX3@zG8#pL9^kM2&bQ7*oN1!yW5$uV4%C6dE0";
		    	  
		    	  if(request.getHeaders().containsKey("prostaff-dev-key")  && request.getHeaders().get("prostaff-dev-key").get(0).equals(val))
		    	  {
		    		  return chain.filter(exchange);
		    	  }
		    	  exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(404));
		    	  return exchange.getResponse().setComplete();
		      }
		      
		      
	    	  if(path.contains("/auth"))
	    	  {
	    		  if(openForAllPaths.contains(path))
	    		  {
	    			  return chain.filter(exchange);
	    		  }
	    		  else if(path.contains("/auth/get-user-fullname") || path.contains("/auth/add-admin")) {}
	    		  
	    		  else
	    		  {
	    			  exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(404));
			    	  return exchange.getResponse().setComplete();
	    		  }
	    	  }
	    	  
	    	  if(path.contains("/organization"))
	    	  {
	    		  if(!path.contains("/organization/remove-upcoming-event/") && !path.contains("/organization/remove-faq/") && !accessibleOrganizationMethods.contains(path))
	    		  {
	    			  exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(404));
			    	  return exchange.getResponse().setComplete();
	    		  }
	    	  }
		      
		      
		      if (request.getHeaders().containsKey("Authorization")){   
		    	  
		    	  AuthRequest req = new AuthRequest(request.getHeaders().get("Authorization").get(0), path);
		    	  
		    	  Integer statusCode = restTemplate.postForObject("http://localhost:5580/auth/validate", req , Integer.class);
		    	  if(statusCode == 1)
		    	  {
		    		  exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
			    	  return exchange.getResponse().setComplete();
		    	  }
		    	  else if(statusCode == 2)
		    	  {
		    		  exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(403));
			    	  return exchange.getResponse().setComplete();
		    	  }
		    	   
		      } else {
		    	  exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
		    	  return exchange.getResponse().setComplete();
		      }
	
		      return chain.filter(exchange);
		    };
		  }
	
		  
		@Override
		public Class<Config> getConfigClass() {
			return Config.class;
		}
	
		@Override
		public Config newConfig() {
			Config c = new Config();
			return c;
		}
	
		public static class Config {}
	
}
