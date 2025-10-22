(ns clj-gephi.statistics-test
  (:require [clj-gephi.graph :as g]
            [clj-gephi.statistics :as stat]
            [clojure.test :refer :all]
            [clj-gephi.project :as p]))

(defn create-test-workspace []
  (p/new-project!)
  (p/current-workspace))

(defn create-star-network
  "Creates a star network with one central node connected to n peripheral nodes"
  [gm n]
  (let [graph (g/undirected-graph gm)
        center (g/node! graph "center" "Center")]
    (doseq [i (range n)]
      (let [node (g/node! graph (str "node-" i) (str "Node " i))]
        (g/edge! graph center node false)))
    graph))

(defn create-directed-chain
  "Creates a directed chain: n1 -> n2 -> n3 -> ... -> nn"
  [gm n]
  (let [graph (g/directed-graph gm)
        nodes (mapv #(g/node! graph (str "n" %) (str "Node " %)) (range n))]
    (doseq [i (range (dec n))]
      (g/edge! graph (nth nodes i) (nth nodes (inc i)) true))
    graph))

(defn create-triangle-network
  "Creates a triangle: three nodes all connected to each other"
  [gm]
  (let [graph (g/undirected-graph gm)
        n1 (g/node! graph "n1" "Node 1")
        n2 (g/node! graph "n2" "Node 2")
        n3 (g/node! graph "n3" "Node 3")]
    (g/edge! graph n1 n2 false)
    (g/edge! graph n2 n3 false)
    (g/edge! graph n3 n1 false)
    graph))

(deftest test-degree-calculation
  (testing "Calculating degree statistics"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-star-network gm 5)
          degree-stat (stat/degree! gm)]
      (is (not (nil? degree-stat)))
      (let [avg-deg (stat/average-degree degree-stat)]
        (is (number? avg-deg))
        ;; Star network with 6 nodes (1 center + 5 peripheral)
        ;; Center has degree 5, others have degree 1
        ;; Average = (5 + 1 + 1 + 1 + 1 + 1) / 6 = 10/6 ≈ 1.67
        (is (> avg-deg 1.5))
        (is (< avg-deg 2.0))))))

(deftest test-degree-column
  (testing "Degree statistics create node columns"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-star-network gm 3)]
      (stat/degree! gm)
      (let [degree-col (stat/column gm stat/degree-idx)]
        (is (not (nil? degree-col)))
        (is (= stat/degree-idx (.getId degree-col)))))))

(deftest test-pagerank-directed
  (testing "PageRank on directed graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-directed-chain gm 4)
          pr (stat/pagerank! gm true)]
      (is (not (nil? pr)))
      ;; Verify PageRank column was created
      (let [pr-col (stat/column gm stat/pagerank-idx)]
        (is (not (nil? pr-col)))))))

(deftest test-pagerank-undirected
  (testing "PageRank on undirected graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-triangle-network gm)
          pr (stat/pagerank! gm false)]
      (is (not (nil? pr)))
      ;; Verify PageRank column was created
      (let [pr-col (stat/column gm stat/pagerank-idx)]
        (is (not (nil? pr-col)))))))

(deftest test-graph-distance-undirected
  (testing "Graph distance metrics on undirected graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-star-network gm 4)
          dist (stat/distance! gm false)]
      (is (not (nil? dist)))
      ;; In a star network, diameter should be 2 (from any peripheral to any other through center)
      (is (= 2.0 (stat/diameter dist)))
      ;; Radius should be 1 (from center to any node)
      (is (= 1.0 (stat/radius dist)))
      ;; Average path length
      (let [avg-dist (stat/avg-distance dist)]
        (is (number? avg-dist))
        (is (> avg-dist 1.0))
        (is (< avg-dist 2.0))))))

(deftest test-graph-distance-directed
  (testing "Graph distance metrics on directed graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-directed-chain gm 5)
          dist (stat/distance! gm true)]
      (is (not (nil? dist)))
      ;; In a chain of 5 nodes, diameter is 4
      (is (= 4.0 (stat/diameter dist))))))

(deftest test-betweenness-column
  (testing "Betweenness centrality column creation"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-star-network gm 5)]
      (stat/distance! gm false)
      (let [betweenness-col (stat/column gm stat/betweenness-idx)]
        (is (not (nil? betweenness-col)))
        (is (= stat/betweenness-idx (.getId betweenness-col)))))))

(deftest test-modularity-single-community
  (testing "Modularity on a simple triangle network"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-triangle-network gm)
          mod (stat/modularity! gm)]
      (is (not (nil? mod)))
      ;; Verify modularity column was created
      (let [mod-col (stat/column gm stat/modularity-idx)]
        (is (not (nil? mod-col)))))))

(deftest test-modularity-two-communities
  (testing "Modularity detection on two connected triangles"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/undirected-graph gm)
          ;; First triangle
          n1 (g/node! graph "n1" "Node 1")
          n2 (g/node! graph "n2" "Node 2")
          n3 (g/node! graph "n3" "Node 3")
          ;; Second triangle
          n4 (g/node! graph "n4" "Node 4")
          n5 (g/node! graph "n5" "Node 5")
          n6 (g/node! graph "n6" "Node 6")]
      ;; Connect first triangle
      (g/edge! graph n1 n2 false)
      (g/edge! graph n2 n3 false)
      (g/edge! graph n3 n1 false)
      ;; Connect second triangle
      (g/edge! graph n4 n5 false)
      (g/edge! graph n5 n6 false)
      (g/edge! graph n6 n4 false)
      ;; Connect the two triangles with a bridge
      (g/edge! graph n3 n4 false)

      (let [mod (stat/modularity! gm)]
        (is (not (nil? mod)))
        ;; Verify modularity column exists
        (let [mod-col (stat/column gm stat/modularity-idx)]
          (is (not (nil? mod-col))))))))

(deftest test-html-report
  (testing "Generating HTML report from statistics"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-triangle-network gm)
          dist (stat/distance! gm false)
          report (stat/html-report dist)]
      (is (string? report))
      (is (> (count report) 0))
      ;; HTML reports should contain basic HTML tags
      (is (or (.contains report "<html")
              (.contains report "<HTML")
              (.contains report "<!"))))))

(deftest test-comprehensive-network-analysis
  (testing "Comprehensive analysis of a network"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          ;; Create a more complex network
          graph (g/undirected-graph gm)
          ;; Create nodes
          nodes (mapv #(g/node! graph (str "n" %) (str "Person " %)) (range 10))]
      ;; Create edges to form communities
      ;; Community 1 (nodes 0-3)
      (g/edge! graph (nth nodes 0) (nth nodes 1) false)
      (g/edge! graph (nth nodes 0) (nth nodes 2) false)
      (g/edge! graph (nth nodes 1) (nth nodes 2) false)
      (g/edge! graph (nth nodes 2) (nth nodes 3) false)

      ;; Community 2 (nodes 4-7)
      (g/edge! graph (nth nodes 4) (nth nodes 5) false)
      (g/edge! graph (nth nodes 5) (nth nodes 6) false)
      (g/edge! graph (nth nodes 6) (nth nodes 7) false)
      (g/edge! graph (nth nodes 4) (nth nodes 7) false)

      ;; Isolated nodes with few connections
      (g/edge! graph (nth nodes 8) (nth nodes 9) false)

      ;; Bridge between communities
      (g/edge! graph (nth nodes 3) (nth nodes 4) false)

      ;; Run all statistics
      (let [degree-stat (stat/degree! gm)
            pagerank-stat (stat/pagerank! gm false)
            distance-stat (stat/distance! gm false)
            modularity-stat (stat/modularity! gm)]

        ;; Verify all statistics were calculated
        (is (not (nil? degree-stat)))
        (is (not (nil? pagerank-stat)))
        (is (not (nil? distance-stat)))
        (is (not (nil? modularity-stat)))

        ;; Verify basic metrics make sense
        (is (= 10 (g/node-count graph)))
        (is (= 11 (g/edge-count graph)))
        (is (> (stat/average-degree degree-stat) 1.0))
        (is (number? (stat/diameter distance-stat)))
        (is (number? (stat/avg-distance distance-stat)))))))
