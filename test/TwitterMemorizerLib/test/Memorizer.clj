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
            "\n")))

      (testing "渡されたシーケンスが[#'あ+']のとき、"
        (testing "「あ」を含む文字列にはtrueを返す"
          (are [text] (true? (match-regex-seq? text [#"あ+"]))
            "あ"
            "あああ"
            "あいうえお"
            "おえいうあ"
            "aあ"
            "0あ"
            "@あ"
            "\nあ"))
        (testing "「あ」を含まない文字列にはfalseを返す"
          (are [text] (false? (match-regex-seq? text [#"あ+"]))
            ""
            "a"
            "@"
            "い"
            "ア"
            "亜"))))

    (testing "execOnStatusは"
      (testing "文字列とstr関数を渡すと渡された文字列を返す"
        (are [text] (= text (execOnStatus text str))
          "az"
          "09"
          "@"
          "あ"
          "日本語"
          "\n")))))
