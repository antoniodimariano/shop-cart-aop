### A demo code written in Java that use Aspect Oriented Programming to manage a shop's cart session data

##Project's workflow

* It creates a Shop instance with Shop.InitShop() method.
* An aspect runs the public method getProdotti() and gets the list of store's products.
* When a user has logged in, another aspect checks if a valid sessionId exists.Elsewhere a new sessionId will be generated.
* An aspect checks if there is a cart associate to the users's sessionId. If there is a cart's data stored in session, it returns its instance. Elsewhere a new cart's instance will be created.