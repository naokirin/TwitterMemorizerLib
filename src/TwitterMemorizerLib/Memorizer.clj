(ns TwitterMemorizerLib.Memorizer
  (:use [TwitterMemorizerLib.Twitter])
  (:use [clojure.contrib.java-utils]))

(import (twitter4j UserStreamAdapter))

(def *saving-regex-seq* (ref []))

(defn match-regex-seq?
  "Return whether the text matches all regex in sequence"
  [text
   regex-seq]
  (if (not-empty regex-seq)
    (reduce #(or %1 %2) (map #(not (nil? (re-find % text))) regex-seq))
    false))

(defn processTweet
  "Process Tweets"
  [status]
  (let [user (.. status getUser getScreenName) text (. status getText)]
    (if (match-regex-seq? text @*saving-regex-seq*)
      (str user ":" text))))

(defn execOnStatus
  "Execution onStatus"
  [text
   fun]
  (if text
    (fun text)))

(defn myAdapter
  "Return UserStreamAdapter Instance"
  []
  (proxy [UserStreamAdapter] []
    (onStatus [status]
        (execOnStatus (processTweet status) println))))

(defn execTwitterMemorizer
  "Execute TwitterMemorizer"
  [consumer-key
   consumer-secret
   token-filename
   regex-seq]
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
      (dosync (ref-set *saving-regex-seq* regex-seq))
      (. stream user))))