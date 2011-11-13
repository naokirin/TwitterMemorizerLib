(ns TwitterMemorizerLib.Stream
  (:import [twitter4j.TwitterStreamFactory]))

(defn createTwitterStreamInstance
  "Return twitter4j.TwitterStream Instance"
  [consumer-key
   consumer-secret]
  (let [stream (. (twitter4j.TwitterStreamFactory.) getInstance)]
      (do
        (. stream setOAuthConsumer consumer-key consumer-secret)
        stream)))

(defn setStreamAccessToken
  [stream
   access-token]
    (. stream setOAuthAccessToken access-token))

(defn addStreamListener
  "Add Listener to Stream Instance"
  [stream
   stream-adapter]
    (. stream addListener stream-adapter))
