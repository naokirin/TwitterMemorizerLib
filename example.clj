(defmemo
  :regex-set [
               ["reply", [], [#"^@"]]
               ["self", [#"Naoki_Rin"], []]
               ["RT or MT", [], [#"^RT" #"^MT"]]
               ])
