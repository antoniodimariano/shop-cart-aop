package main;

//import javax.security.auth.callback.CallbackHandler;
//import javax.security.auth.login.LoginContext;
//import javax.security.auth.login.LoginException;

//import carretti.Carrello;
//import carretti.Client;
import carretti.Server;
//import utils.LoginCallbackHandler;
import valueobject.Response;


public class Main {

		public static void main(String[] args) {
			
			final String username = "user1";
			final String password = "user1";

			Server server = new Server();
			Response ret  = server.login(username, password);
			
			System.out.println("* Main:"+ret.getEsito());
			System.out.println("* Main:	ret.getSessionData(): "+ret.getSessionCode());

			
			
			

			
			
/*			
			System.setProperty("java.security.auth.login.config", "jaas.config");


			boolean loginStatus = true;

			//CallbackHandler handler = new LoginCallbackHandler(username,password);

			
			LoginContext ctx = null;
		    try {
		      ctx = new LoginContext("CarrettiLogin",new LoginCallbackHandler(username, password));
		    } catch(LoginException le) {
		      System.err.println("LoginContext cannot be created. "+ le.getMessage());
		      System.exit(-1);
		    } catch(SecurityException se) {
		      System.err.println("LoginContext cannot be created. "+ se.getMessage());
		    }
		    try {
		      ctx.login();
		    } catch(LoginException le) {
		     System.out.println("Authentication failed. " + le.getMessage());
		     System.exit(-1);
		    }
		    System.out.println("Authentication succeeded.");
		    System.exit(-1);
		  }
			
			
			
			/*
			
			try {

				//LoginContext loginContext = new LoginContext("CarrettiLogin" , handler);
				LoginContext loginContext = new LoginContext("CarrettiLogin",new LoginCallbackHandler(username, password));
				loginContext.login();

			} catch (LoginException e) {
				System.out.println("loginStatus");
				loginStatus = false;
				e.printStackTrace();

			}

			*/
		}
	}
	

