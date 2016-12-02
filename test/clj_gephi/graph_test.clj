(ns clj-gephi.graph-test
  (:require [clj-gephi.graph :refer :all]
            [clojure.test :refer :all]
            [clj-gephi.project :as p]))
(def proj (p/new-project!))
(def wp (p/new-workspace! proj))


(deftest test-node
  (testing "testing node multi-method"
    (let [gm1 (graph-model wp)
          g1  (directed-graph gm1)
          n1  (node gm1 "n1" "n1 label")
          _   (add-node! g1 n1)
          n2  (node g1 "n1" "n2 label") ;; already created return n1
          ]
      (is (= "n1" (.getId n1)))
      (is (= "n1 label" (.getLabel n1)))
      (is (= "n1 label" (.getLabel n2))))))

