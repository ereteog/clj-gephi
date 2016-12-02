(ns clj-gephi.graph-test
  (:require [clj-gephi.graph :as g]
            [clojure.test :refer :all]
            [clj-gephi.project :as p]))
(def proj (p/new-project!))
(def wp (p/new-workspace! proj))


(deftest test-node
  (testing "testing node multi-method"
    (let [gm1 (g/graph-model wp)
          g1  (g/directed-graph gm1)
          n1  (g/node gm1 "n1" "n1 label") ;; create from GraphModel instance
          n2  (g/node g1 "n2" "n2 label") ;; create from graph with new id
          _   (g/add-node! g1 n1)
          n3  (g/node g1 "n1" "n1 with another label") ;; created from graph with existing id
          ]
      (is (= "n1" (.getId n1)))
      (is (= "n1 label" (.getLabel n1)))
      (is (= "n2" (.getId n2)))
      (is (= "n2 label" (.getLabel n2)))
      (is (= "n1 label" (.getLabel n3))))))

