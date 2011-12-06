(ns TwitterMemorizerLib.core
  (:gen-class)
  (:use TwitterMemorizerLib.Memorizer TwitterMemorizerLib.DSL)
  (:use [clojure.contrib.command-line :only (with-command-line)])
  (:use [clojure.contrib.java-utils])
  (:require [clojure.contrib.string :as s]))

(def *consumer-key* "")
(def *consumer-secret* "")
(def *token-file-name* "accesstoken")
(def *regex-file-name* "example.clj")
(def *json-file* "jsonfile")

(defn print-json
  "Print json file"
  [json-file]
  (let [jf (input-json json-file)]
    (doall (map (fn [key]
           (println
             "\n" key "\n"
             (doall (map (fn [ts] (str
                     "id:" (get ts :id)
                     " user:" (get ts :user)
                     " tweet:" (get ts :tweet)
                     " at:" (get ts :at)
                     "\n"))
               (get jf key)))))
      (keys jf)))))

(defn -main
[& args]
(with-command-line args
  "exec memorizer and show json-file"
  [[print p "show json-file" nil]
   [label l "show label" nil]
   [file f "saveing file name" *json-file* ]]
  (if print (print-json print)
    (exec-twitter-memorizer
      *consumer-key*  *consumer-secret* *token-file-name*
      (read-file *regex-file-name*) file
      read-line))))
