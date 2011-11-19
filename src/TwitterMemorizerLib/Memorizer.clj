(ns TwitterMemorizerLib.Memorizer
  (:use [TwitterMemorizerLib.Twitter])
  (:use [clojure.contrib.java-utils])
  (:import [twitter4j UserStreamAdapter]))

(defstruct name-and-regex
  :regex-name
  :screenname-regex-seq
  :tweet-regex-seq)

(def *saving-regex-seq* (ref []))

(defn match-regex-seq?
  "Return whether the text matches all regex in sequence"
  [text
   regex-seq]
  (if (not-empty regex-seq)
    (reduce #(or %1 %2) (map #(not (nil? (re-find % text))) regex-seq))
    true))

(defn processTweet
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

(defn myAdapter
  "Return UserStreamAdapter Instance"
  []
  (proxy [UserStreamAdapter] []
    (onStatus [status]
      (processTweet status @*saving-regex-seq*))))

(defn execTwitterMemorizer
  "Execute TwitterMemorizer"
  [consumer-key
   consumer-secret
   token-filename
   name-and-regex-seq]
  (let [twitter (createTwitterInstance consumer-key consumer-secret),
        access-token
        (if (. (clojure.contrib.java-utils/file token-filename) exists)
          (loadAccessToken token-filename)
          (let [tok (authorizeOAuth twitter println read-line)]
            (do (saveAccessToken tok token-filename) tok))),
        stream (createTwitterStreamInstance consumer-key  consumer-secret)]
    (do
      (setOAuthAccessToken stream access-token)
      (addStreamListener stream (myAdapter))
      (dosync (ref-set *saving-regex-seq* name-and-regex-seq))
      (. stream user))))