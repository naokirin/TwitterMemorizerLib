(defproject TwitterMemorizerLib "1.0.0-SNAPSHOT"
  :description """TwitterMemorizerLibはtwitter4jの機能を用いてTwitterのHomeのタイムラインを取得し、
  指定された正規表現にマッチするツイートを保存する機能を提供します"""
  :dependencies [[org.clojure/clojure "[1.2.1]"]
                   [org.clojure/clojure-contrib "[1.2,)"]
                   [org.twitter4j/twitter4j-core "[1.2,)"]
                   [org.twitter4j/twitter4j-stream "[1.2,)"]]
  :main TwitterMemorizerLib.Main)
