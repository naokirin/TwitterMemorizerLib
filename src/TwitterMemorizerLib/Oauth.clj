(ns TwitterMemorizerLib.Oauth
  (:import [twitter4j.TwitterFactory])
  (:import [java.io ObjectInputStream ObjectOutputStream FileInputStream FileOutputStream File]))

(defn createTwitterInstance
  "Return twitter4j.Twitter Instance with setting consumer key and consumer secret"
  [consumer-key
   consumer-secret]
  (let [twitter (. (twitter4j.TwitterFactory.) getInstance)]
    (do
      (. twitter setOAuthConsumer consumer-key consumer-secret)
      twitter)))

(defn authorizeOAuth
  "Return access token with oauth-authorization"
  [twitter
   output-url-fun
   input-pin]
  (let [token (. twitter getOAuthRequestToken)]
    (do
      (output-url-fun (. token getAuthorizationURL))
      (. twitter getOAuthAccessToken token (if (fn? input-pin) (input-pin) input-pin)))))

(defn saveAccessToken
  "Save AccessToken"
  [access-token
   file-name]
  (with-open [os (new ObjectOutputStream (new FileOutputStream (new File file-name)))]
    (. os writeObject access-token)))

(defn loadAccessToken
  "Load AccessToken"
  [file-name]
  (with-open [is (new ObjectInputStream (new FileInputStream (new File file-name)))]
    (. is readObject)))

(defn setOAuthAccessToken
  "Set AccessToken to Twitter Instance"
  [twitter
   access-token]
    (. twitter setOAuthAccessToken access-token))