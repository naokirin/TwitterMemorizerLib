(ns TwitterMemorizerLib.Twitter
  (:import [twitter4j.TwitterFactory])
  (:import [twitter4j.TwitterStreamFactory])
  (:import [java.io ObjectInputStream ObjectOutputStream FileInputStream FileOutputStream File]))

(defn create-twitter-instance
  "Return twitter4j.Twitter Instance with setting consumer key and consumer secret"
  [consumer-key
   consumer-secret]
  (let [twitter (. (twitter4j.TwitterFactory.) getInstance)]
    (do
      (. twitter setOAuthConsumer consumer-key consumer-secret)
      twitter)))

(defn create-twitter-stream-instance
  "Return twitter4j.TwitterStream Instance"
  [consumer-key
   consumer-secret]
  (let [stream (. (twitter4j.TwitterStreamFactory.) getInstance)]
    (do
      (. stream setOAuthConsumer consumer-key consumer-secret)
      stream)))

(defn add-stream-listener
  "Add Listener to Stream Instance"
  [stream
   stream-adapter]
  (. stream addListener stream-adapter))

(defn authorize-oauth
  "Return access token with oauth-authorization"
  [twitter
   output-url-fun
   input-pin]
  (let [token (. twitter getOAuthRequestToken)]
    (do
      (output-url-fun (. token getAuthorizationURL))
      (. twitter getOAuthAccessToken token (if (fn? input-pin) (input-pin) input-pin)))))

(defn save-access-token
  "Save AccessToken"
  [access-token
   file-name]
  (with-open [os (new ObjectOutputStream (new FileOutputStream (new File file-name)))]
    (. os writeObject access-token)))

(defn load-access-token
  "Load AccessToken"
  [file-name]
  (with-open [is (new ObjectInputStream (new FileInputStream (new File file-name)))]
    (. is readObject)))

(defn set-oauth-access-token
  "Set AccessToken to Twitter Instance"
  [twitter
   access-token]
    (. twitter setOAuthAccessToken access-token))