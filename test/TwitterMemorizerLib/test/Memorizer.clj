(ns TwitterMemorizerLib.test.Memorizer
  (:use [TwitterMemorizerLib.Memorizer])
  (:use [clojure.test]))

(deftest Memorizer
  (testing "Memorizer."
    (testing "match-regex-seq?は"
      (testing "渡されたシーケンスが空のとき、falseを返す"
        (are [text] (false? (match-regex-seq? text []))
          ""
          "@"
          "123"
          "alphabet"
          "日本語"
          "\n"))

      (testing "渡されたシーケンスが[#'']のとき、常にtrueを返す"
        (are [text] (true? (match-regex-seq? text [#""]))
          ""
          "@"
          "123"
          "alphabet"
          "日本語"
          "\n"))

      (testing "渡されたシーケンスが[#'[a-z]+']のとき、"
        (testing "小文字のアルファベットを含む文字列にマッチしtrueを返す"
          (are [text] (true? (match-regex-seq? text [#"[a-z]+"]))
            "az"
            "1a"
            "@a"
            "Az"
            "日本語a"
            "\na"))
        (testing "小文字のアルファベットを含まない文字列にはfalseを返す"
          (are [text] (false? (match-regex-seq? text [#"[a-z]+"]))
            "AZ"
            "019"
            "@"
            "日本語"
            "\n"))))))