(ns TwitterMemorizerLib.Memorizer
  (:use [TwitterMemorizerLib.Twitter])
  (:use [clojure.contrib.java-utils])
  (:import [twitter4j UserStreamAdapter]))

(defstruct name-and-regex
  :regex-name
  :screenname-regex-seq
  :tweet-regex-seq)

(def *saving-regex-seq* (ref []))

(defn- match-regex-seq?
  "Return whether the text matches all regex in sequence"
  [text
   regex-seq]
  (if (not-empty regex-seq)
    (reduce #(or %1 %2) (map #(not (nil? (re-find % text))) regex-seq))
    true))

(defn- process-tweet
  "Process Tweets"
  [status name-and-regex-seq]
  (dorun (map
    #(let [user (.. status getUser getScreenName) text (. status getText),
           regex-name (:regex-name %),
           screenname-regex-seq (:screenname-regex-seq %),
           tweet-regex-seq (:tweet-regex-seq %)]
      (if (and (match-regex-seq? text tweet-regex-seq) (match-regex-seq? user screenname-regex-seq))
        (println regex-name " / " user ":" text)))
           name-and-regex-seq)))

(defn- my-adapter
  "Return UserStreamAdapter Instance"
  []
  (proxy [UserStreamAdapter] []
    (onStatus [status]
      (process-tweet status @*saving-regex-seq*))))

(defn exec-twitter-memorizer
  "Execute TwitterMemorizer"
  [consumer-key
   consumer-secret
   token-filename
   name-and-regex-seq
   shutdown-fun]
  (let [twitter (create-twitter-instance consumer-key consumer-secret),
        access-token
        (if (. (clojure.contrib.java-utils/file token-filename) exists)
          (load-access-token token-filename)
          (let [tok (authorize-oauth twitter println read-line)]
            (do (save-access-token tok token-filename) tok))),
        stream (create-twitter-stream-instance consumer-key  consumer-secret)]
    (do
      (set-oauth-access-token stream access-token)
      (add-stream-listener stream (my-adapter))
      (dosync (ref-set *saving-regex-seq* name-and-regex-seq))
      (. stream user)
      (shutdown-fun)
      (. stream shutdown))))