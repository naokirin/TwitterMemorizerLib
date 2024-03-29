(ns TwitterMemorizerLib.DSL
  (:use [clojure.contrib.def]))

(def ^{:private true} f nil)

(defn read-file
  "Read DSL file"
  ([file]
    (try (binding [*ns* (the-ns 'TwitterMemorizerLib.DSL)]
      (load-file file) f)
      (catch java.io.FileNotFoundException _))))

(defn- expand-set
  "Expand DSL set"
  ([m coll]
    (if (not-empty coll)
      (let [c (first coll),
            mp (merge m (hash-map :regex-name (-> c first)
                                    :screenname-regex-seq (-> c rest first)
                                    :tweet-regex-seq (-> c rest second)))]
        (expand-set mp (rest coll)))
    m))
  ([coll] (expand-set #{} coll)))

(defmacro- defmemo
  [& args]
  `(let [m# (hash-map ~@args)]
     (alter-var-root #'f
       (fn [_#]
         (if-let [s# (:regex-set m#)]
           (expand-set s#) #{})))
      #'f))