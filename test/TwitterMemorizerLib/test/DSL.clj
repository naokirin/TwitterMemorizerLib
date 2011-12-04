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
        (testing "[['a' 'b' 'c']]を渡すと#{{:regex-name 'a', :screenname-regex-seq 'b', :tweet-regex-seq 'c'}}を返す"
          (is (= #{{:regex-name "a", :screenname-regex-seq "b", :tweet-regex-seq "c"}} (expand-set [["a" "b" "c"]]))))
        (testing "[[1 2 3]]を渡すと#{{:regex-name 1, :screenname-regex-seq 2, :tweet-regex-seq 3}}を返す"
          (is (= #{{:regex-name 1, :screenname-regex-seq 2, :tweet-regex-seq 3}} (expand-set [[1 2 3]]))))
        (testing "[[1 2 3] [4 5 6]]を渡すと#{{:regex-name 1, :screenname-regex-seq 2, :tweet-regex-seq 3}
                                          {:regex-name 4, :screenname-regex-seq 5, :tweet-regex-seq 6}}を返す"
          (is (= #{{:regex-name 1, :screenname-regex-seq 2, :tweet-regex-seq 3}
                   {:regex-name 4, :screenname-regex-seq 5, :tweet-regex-seq 6}} (expand-set [[1 2 3] [4 5 6]]))))))))
