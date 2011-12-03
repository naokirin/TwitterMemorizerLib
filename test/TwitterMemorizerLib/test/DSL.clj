(ns TwitterMemorizerLib.test.DSL
  (:use [TwitterMemorizerLib.DSL])
  (:use [clojure.test]))
(defmacro with-private-fns [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context."
    `(let ~(reduce #(conj %1 %2 `(ns-resolve '~ns '~%2)) [] fns)
         ~@tests))

(with-private-fns [TwitterMemorizerLib.DSL [expand-set]]
  (deftest DSL
    (testing "DSL."
      (testing "expand-setは"
        (testing "[]を渡すと#{}を返す"
          (is (= #{} (expand-set []))))
        (testing "[['a' 'b' 'c']]を渡すと[{:regex-name 'a', :screenname-regex-seq 'b', :tweet-regex-seq} 'c'}]を返す"
          (is (= #{{:regex-name "a", :screenname-regex-seq "b", :tweet-regex-seq "c"}} (expand-set [["a" "b" "c"]]))))))))
