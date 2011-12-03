(ns TwitterMemorizerLib.DSL
  (:use [clojure.contrib.def]))

(def ^{:private true} f nil)

(defn read-file
  ([file]
     (try (binding [*ns* (the-ns 'TwitterMemorizerLib.DSL)]
       (load-file file)
       f)
       (catch java.io.FileNotFoundException _))))

(defn- expand-set
  ([m coll]
    (if (not-empty coll)
      (let [mp (merge m (hash-map :regex-name (-> coll first first)
                                     :screenname-regex-seq (-> coll first rest first)
                                     :tweet-regex-seq (-> coll first rest second)))]
      (expand-set mp (rest coll)))
    m))
  ([coll] (expand-set #{} coll)))

(defmacro- defmemo
  [memo-name & args]
  `(let [m# (hash-map ~@args)]
     (alter-var-root #'f
       (fn [_#]
         (if-let [s# (:regex-set m#)]
           (expand-set s#) #{})))
        #'f))