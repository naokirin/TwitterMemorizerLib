(ns TwitterMemorizerLib.Memorizer
  (:use [clojure.contrib.json :only [json-str read-json]])
  (:use [clojure.java.io :only [reader file]])
  (:import [twitter4j UserStreamAdapter])
  (:use [TwitterMemorizerLib.Twitter]))

(defstruct name-and-regex
  :regex-name
  :screenname-regex-seq
  :tweet-regex-seq)

(defn output-json
  "Output json file"
  [coll
   file-name]
  (spit file-name (json-str coll)))

(defn input-json
  "Input json file and Return hashmap"
  [file-name]
  (if (. (file file-name) exists)
    (with-open [r (reader file-name)] (read-json r))
    (hash-map)))

(defn- match-regex-seq?
  "Return whether the text matches all regex in sequence"
  [text
   regex-seq]
  (if (not-empty regex-seq)
    (reduce #(or %1 %2) (map #(not (nil? (re-find % text))) regex-seq))
    true))

(def ^{:private true} save (ref {}))

(defn- process-tweet
  "Process Tweets"
  [status name-and-regex-seq json-file]
    (doall (map
      #(let [user (.. status getUser getScreenName) text (. status getText) at (str (. status getCreatedAt)) id (. status getId),
             regex-name (:regex-name %),
             screenname-regex-seq (:screenname-regex-seq %),
             tweet-regex-seq (:tweet-regex-seq %)]
        (if (and (match-regex-seq? text tweet-regex-seq) (match-regex-seq? user screenname-regex-seq))
          (let [s (let [rn (get @save regex-name), data {:id id, :user user, :tweet text, :at at}]
                                    (if rn (assoc (dissoc @save regex-name) regex-name (conj rn data))
                                      (assoc @save regex-name [data])))]
            (dosync (ref-set save s))
            (output-json s json-file))))
      name-and-regex-seq)))

(defn- my-adapter
  "Return UserStreamAdapter Instance"
  [regex-seq
   json-file]
  (proxy [UserStreamAdapter] []
    (onStatus [status]
      (process-tweet status regex-seq json-file))))

(defn- set-access-token
  "Return Access token"
  [twitter
   token-filename]
  (if (. (file token-filename) exists)
    (load-access-token token-filename)
    (let [tok (authorize-oauth twitter println read-line)]
      (do (save-access-token tok token-filename) tok))))

(defn exec-twitter-memorizer
  "Execute TwitterMemorizer"
  [consumer-key
   consumer-secret
   token-filename
   name-and-regex-seq
   json-file
   shutdown-fun]
  (let [twitter (create-twitter-instance consumer-key consumer-secret),
        access-token (set-access-token twitter token-filename),
        stream (create-twitter-stream-instance consumer-key  consumer-secret)]
    (do
      (dosync (ref-set save (input-json json-file)))
      (set-oauth-access-token stream access-token)
      (add-stream-listener stream (my-adapter name-and-regex-seq json-file))
      (. stream user)
      (shutdown-fun)
      (. stream shutdown))))