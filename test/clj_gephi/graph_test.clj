(ns clj-gephi.graph-test
  (:require [clj-gephi.graph :as g]
            [clojure.test :refer :all]
            [clj-gephi.project :as p]))

(defn create-test-workspace []
  (p/new-project!)
  (p/current-workspace))

(deftest test-node-creation
  (testing "Node creation from GraphModel"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/directed-graph gm)
          n1 (g/node gm "n1" "Node 1")
          n2 (g/node gm "n2" "Node 2")]
      (is (= "n1" (.getId n1)))
      (is (= "Node 1" (.getLabel n1)))
      (is (= "n2" (.getId n2)))
      (is (= "Node 2" (.getLabel n2))))))

(deftest test-node-operations
  (testing "Adding and counting nodes"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/directed-graph gm)
          n1 (g/node gm "n1" "Node 1")
          n2 (g/node gm "n2" "Node 2")]
      (g/add-node! graph n1)
      (is (= 1 (g/node-count graph)))
      (g/add-node! graph n2)
      (is (= 2 (g/node-count graph)))
      (is (g/has-node? graph "n1"))
      (is (g/has-node? graph "n2"))
      (is (not (g/has-node? graph "n3"))))))

(deftest test-node-retrieval
  (testing "Retrieving nodes by ID"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/directed-graph gm)]
      (g/node! graph "n1" "Node 1")
      (g/node! graph "n2" "Node 2")
      (let [retrieved-n1 (g/node-by-id graph "n1")]
        (is (not (nil? retrieved-n1)))
        (is (= "Node 1" (.getLabel retrieved-n1))))
      (let [retrieved-n2 (g/node-by-id graph "n2")]
        (is (not (nil? retrieved-n2)))
        (is (= "Node 2" (.getLabel retrieved-n2)))))))

(deftest test-edge-creation
  (testing "Creating edges between nodes"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/directed-graph gm)
          n1 (g/node! graph "n1" "Node 1")
          n2 (g/node! graph "n2" "Node 2")
          e1 (g/edge gm n1 n2 true)]
      (is (= n1 (g/source e1)))
      (is (= n2 (g/target e1))))))

(deftest test-edge-operations
  (testing "Adding and counting edges"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/directed-graph gm)
          n1 (g/node! graph "n1" "Node 1")
          n2 (g/node! graph "n2" "Node 2")
          n3 (g/node! graph "n3" "Node 3")]
      (is (= 0 (g/edge-count graph)))
      (g/edge! graph n1 n2 true)
      (is (= 1 (g/edge-count graph)))
      (g/edge! graph n2 n3 true)
      (is (= 2 (g/edge-count graph)))
      (g/edge! graph n3 n1 true)
      (is (= 3 (g/edge-count graph))))))

(deftest test-graph-types
  (testing "Creating different graph types"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          directed (g/directed-graph gm)
          undirected (g/undirected-graph gm)
          n1 (g/node! directed "n1" "Node 1")
          n2 (g/node! directed "n2" "Node 2")]
      (is (not (nil? directed)))
      (is (not (nil? undirected)))
      (is (= 2 (g/node-count directed)))
      (is (= 2 (g/node-count undirected))))))

(deftest test-clear-nodes
  (testing "Clearing all nodes from graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/directed-graph gm)]
      (g/node! graph "n1" "Node 1")
      (g/node! graph "n2" "Node 2")
      (g/node! graph "n3" "Node 3")
      (is (= 3 (g/node-count graph)))
      (g/clear-nodes graph)
      (is (= 0 (g/node-count graph))))))

(deftest test-all-nodes-and-edges
  (testing "Retrieving all nodes and edges"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/directed-graph gm)
          n1 (g/node! graph "n1" "Node 1")
          n2 (g/node! graph "n2" "Node 2")
          n3 (g/node! graph "n3" "Node 3")]
      (g/edge! graph n1 n2 true)
      (g/edge! graph n2 n3 true)
      (let [nodes (g/all-nodes graph)
            edges (g/all-edges graph)]
        (is (= 3 (.size nodes)))
        (is (= 2 (.size edges)))))))

(deftest test-simple-network
  (testing "Creating a simple social network"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/undirected-graph gm)
          ;; Create users
          alice (g/node! graph "alice" "Alice")
          bob (g/node! graph "bob" "Bob")
          charlie (g/node! graph "charlie" "Charlie")
          diana (g/node! graph "diana" "Diana")]
      ;; Create friendships
      (g/edge! graph alice bob false)
      (g/edge! graph alice charlie false)
      (g/edge! graph bob charlie false)
      (g/edge! graph charlie diana false)
      ;; Verify network structure
      (is (= 4 (g/node-count graph)))
      (is (= 4 (g/edge-count graph)))
      (is (g/has-node? graph "alice"))
      (is (g/has-node? graph "diana")))))

