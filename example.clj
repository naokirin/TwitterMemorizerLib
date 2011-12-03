(defmemo
  :regex-set [
               ["all", [], []]
               ["reply", [], [#"^@"]]
               ["self", [#"naoki_rin"], []]
               ["RT or MT", [], [#"^RT" #"^MT"]]
               ])