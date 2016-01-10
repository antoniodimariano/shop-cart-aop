package aspetti;

import java.util.Calendar;

import services.CartServiceImpl;
import services.ClientServiceImpl;
import services.SessionServiceImpl;
import utils.GenerateId;
import valueobject.Request;
import valueobject.Response;
import carretti.Carrello;
import carretti.Server;
import carretti.Session;
import carretti.Shop;

public aspect LoginAspect {

	String fake_session = "FAKE_SESSION";
	final long TRENTA_MINUTI_LONG = 30L * 1000L * 60L;

	pointcut trapUserLogin(String username, String password) : 
			args(username,password) && 
			call(public Response login(String,String));

	pointcut trapLoginExecution(Server server, String username, String password):
			execution(public Response Server.login(String, String)) &&
			target(server) &&
			args(username,password);

	pointcut trapCartInit(Carrello cart) : 
			execution(Carrello.new()) &&
			target(cart);

	/* pointcut Server */

	pointcut trace_server(Server srv, String s, int q, Request r): 
			args(s,q,r) && target(srv) &&
			( 		call(public * getListaProdotti(Request)) ||
					call(public void addProdotto(String, int, Request)) ||
					call(public void removeProdotto(String,int,Request)) 
			);

	
	pointcut trap_ShopInit(Shop shop) :
			target(shop) &&
				execution(Shop.new());

	pointcut logout(Server srv):  target (srv)  && call(public void logout());

	void around(Server srv) : logout(srv) {
		System.out.println("*[Aspect]*: Aroud on logout ");

		SessionServiceImpl.getInstance().destroySessionByKey(
				srv.getSession().getCodice());
		srv.setSession(null);
		proceed(null);
	}

	after(Shop shop) : trap_ShopInit(shop) {
		shop.getProdotti();
		System.out.println("*[Aspect]*: Inserting store's products");
	}

	/*
	 * Verifies if a valid session has been created
	 */
	before(Server srv, String s, int q, Request req) : trace_server(srv, s,q,req) {
		if (req.getSessionCode().equals(fake_session)) {
			System.out.println("*[Aspect]*:Session Error on "
					+ thisJoinPoint.getSignature());
		}
		/* checks session's timestamp */
		if (checkSessionTimestamp(srv.getSession())) {
			System.out.println("*[Aspect]*: Session valid");
		} else {
			System.out
					.println("*[Aspect]*: Session expired. Redirect to logout");
			srv.logout();
		}
	}

	/* Aspect on the return's value of the login() */
	after(Server server, String username, String password) returning(Response r): trapLoginExecution(server,username,password) {
		System.out.println("*[Aspect]* after return's value ="
				+ r.getEsito());

		/* login ok */
		if (r.getEsito()) {
			System.out.println("*[Aspect]* Login ok ");

			/*
			 * checks if we have seen before the same user
			 */
			String userHasSession = isReturnedUser(username);

			/*
			 * returns the session or creates a new one
			 */
			String userSessionid = (userHasSession != null && !userHasSession
					.isEmpty()) ? userHasSession : initSessionId();
			Session session = setSession(userSessionid);
			/* stores the session code */
			r.setSessionCode(session.getCodice());
			/* persists the session */
			persistSession(session);
			/* stores session into the server's instance  */
			server.setSession(session);
		} else
			System.out.println("*[Aspect]* Login failed");
	}

	/*
	 * @param user login 
	 * Finds user by email 
	 *
	 */
	private String isReturnedUser(String login) {
		ClientServiceImpl cl = new ClientServiceImpl();
		String ret = cl.findUserByEmail(login);
		if (ret != null && !ret.isEmpty()) {
			return ret;
		}
		return null;
	}

	/*
	 * Session ID generation 
	 */
	private String initSessionId() {
		GenerateId myId = new GenerateId();
		String sessionID = myId.generateRandomId();
		return sessionID;
	}

	/*
	 * @param sessionID 
	 * 
	 * Inits a new session instance and save the sessionId and the user
	 * todo: to integrate with newest cart's functions
	 * 
	 */
	private Session setSession(String sessionID) {
		Session session = new Session();

		long creationDate = Calendar.getInstance().getTime().getTime();
		session.setCodice(sessionID);
		session.setCreazione(creationDate);
		session.setCarrello(getUserCart(sessionID));
		return session;
	}

	/*
	 * @param Session sessionID
	 * @return a Carrello's instance
	 *
	 * Checks if a cart instance has been stored in session
	 * 
	 */
	private Carrello getUserCart(String sessionID) {

		CartServiceImpl cartService = new CartServiceImpl();
		try {
			Carrello myCart = cartService.getCartBySessionId(sessionID);
			System.out.println("*[Aspect]* Carrello.id :" + myCart.getId());
			return myCart;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * @param Session session
	 *  
	 * It simulates the data persistence in session. It stores a key on a Singleton class
	 * 
	 */
	private void persistSession(Session session) {
		SessionServiceImpl.getInstance().saveSession(session);
		System.out.println("*[Aspect]* SESSION HAS BEEN STORED :"
				+ SessionServiceImpl.getInstance()
						.findSessionByKey(session.getCodice()).getCodice());

	}

	/*
	 * @param Session session
	 * 
	 * @return Boolean 
	 * It simulates a timestamp check
	 *
	 */
	private Boolean checkSessionTimestamp(Session session) {
		Long sessionCreated = session.getCreazione();
		Long now = Calendar.getInstance().getTime().getTime();
		return now - sessionCreated > TRENTA_MINUTI_LONG;

	}
}
