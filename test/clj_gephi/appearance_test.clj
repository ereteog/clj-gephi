(ns clj-gephi.appearance-test
  (:require [clj-gephi.graph :as g]
            [clj-gephi.appearance :as app]
            [clj-gephi.statistics :as stat]
            [clojure.test :refer :all]
            [clj-gephi.project :as p])
  (:import [java.awt Color]))

(defn create-test-workspace []
  (p/new-project!)
  (p/current-workspace))

(defn create-test-network
  "Creates a network with varying node characteristics"
  [gm]
  (let [graph (g/undirected-graph gm)
        ;; Create a hub node
        hub (g/node! graph "hub" "Hub")
        ;; Create peripheral nodes
        nodes (mapv #(g/node! graph (str "n" %) (str "Node " %)) (range 5))]
    ;; Connect hub to all others
    (doseq [node nodes]
      (g/edge! graph hub node false))
    ;; Connect some peripherals to each other
    (g/edge! graph (nth nodes 0) (nth nodes 1) false)
    (g/edge! graph (nth nodes 1) (nth nodes 2) false)
    (g/edge! graph (nth nodes 3) (nth nodes 4) false)
    graph))

(deftest test-appearance-model-creation
  (testing "Creating appearance model"
    (let [am (app/appearance-model)]
      (is (not (nil? am))))))

(deftest test-degree-ranking-function
  (testing "Creating degree ranking function"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/degree! gm)
          am (app/appearance-model)
          ranking (app/degree-ranking am graph gm)]
      (is (not (nil? ranking))))))

(deftest test-pagerank-ranking-function
  (testing "Creating PageRank ranking function"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/pagerank! gm false)
          am (app/appearance-model)
          ranking (app/pagerank-ranking am graph gm)]
      (is (not (nil? ranking))))))

(deftest test-betweenness-ranking-function
  (testing "Creating betweenness centrality ranking function"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/distance! gm false)
          am (app/appearance-model)
          ranking (app/betweenness-ranking am graph gm)]
      (is (not (nil? ranking))))))

(deftest test-color-degree-ranking-function
  (testing "Creating color-by-degree ranking function"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          am (app/appearance-model)
          ranking (app/color-degree-ranking am graph)]
      (is (not (nil? ranking))))))

(deftest test-size-by-degree
  (testing "Sizing nodes by degree"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/degree! gm)
          am (app/appearance-model)
          result (app/size-by-degree! graph am gm 5 25)]
      (is (not (nil? result)))
      (is (= am result)))))

(deftest test-size-by-pagerank
  (testing "Sizing nodes by PageRank"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/pagerank! gm false)
          am (app/appearance-model)
          result (app/size-by-pagerank! graph am gm 5 30)]
      (is (not (nil? result)))
      (is (= am result)))))

(deftest test-size-by-betweenness
  (testing "Sizing nodes by betweenness centrality"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/distance! gm false)
          am (app/appearance-model)
          result (app/size-by-betweenness! graph am gm 5 30)]
      (is (not (nil? result)))
      (is (= am result)))))

(deftest test-color-by-degree
  (testing "Coloring nodes by degree"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          am (app/appearance-model)
          colors [(Color. 0xFEF0D9) (Color. 0xB30000)]
          positions [0.0 1.0]
          result (app/color-by-degree! am graph colors positions)]
      (is (not (nil? result)))
      (is (= am result)))))

(deftest test-color-by-modularity
  (testing "Coloring nodes by modularity class (community)"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/modularity! gm)
          am (app/appearance-model)
          result (app/color-by-modularity! am graph gm)]
      (is (not (nil? result)))
      (is (= am result)))))

(deftest test-color-by-partition-with-custom-palette
  (testing "Coloring nodes by partition with custom palette function"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/modularity! gm)
          am (app/appearance-model)
          result (app/color-by-modularity! am graph gm app/random-palette)]
      (is (not (nil? result)))
      (is (= am result)))))

(deftest test-generate-palette
  (testing "Generating color palette"
    (let [palette (app/generate-palette 5)]
      (is (not (nil? palette)))
      (is (= 5 (count (.getColors palette)))))))

(deftest test-random-palette
  (testing "Generating random color palette"
    (let [palette (app/random-palette 8)]
      (is (not (nil? palette)))
      (is (= 8 (count (.getColors palette)))))))

(deftest test-partition-function-creation
  (testing "Creating partition function for modularity"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/modularity! gm)
          am (app/appearance-model)
          func (app/partition-function am graph gm stat/modularity-idx)]
      (is (not (nil? func))))))

(deftest test-combined-appearance-styling
  (testing "Combining size and color styling"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          _ (stat/degree! gm)
          _ (stat/pagerank! gm false)
          _ (stat/modularity! gm)
          am (app/appearance-model)]

      ;; Apply both size and color
      (app/size-by-pagerank! graph am gm 8 25)
      (app/color-by-modularity! am graph gm)

      ;; Verify appearance model is still valid
      (is (not (nil? am))))))

(deftest test-multiple-centrality-metrics-styling
  (testing "Styling with different centrality metrics"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-test-network gm)
          ;; Calculate all centrality metrics
          _ (stat/degree! gm)
          _ (stat/pagerank! gm false)
          _ (stat/distance! gm false)
          am (app/appearance-model)]

      ;; Test sizing by each metric
      (app/size-by-degree! graph am gm 5 20)
      (is (not (nil? am)))

      (app/size-by-pagerank! graph am gm 5 20)
      (is (not (nil? am)))

      (app/size-by-betweenness! graph am gm 5 20)
      (is (not (nil? am))))))

(deftest test-appearance-on-larger-network
  (testing "Applying appearance transformations to larger network"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/undirected-graph gm)]

      ;; Create larger network with multiple communities
      ;; Community 1
      (doseq [i (range 5)]
        (g/node! graph (str "a" i) (str "Alpha " i)))
      (let [a-nodes (mapv #(g/node-by-id graph (str "a" %)) (range 5))]
        (g/edge! graph (nth a-nodes 0) (nth a-nodes 1) false)
        (g/edge! graph (nth a-nodes 1) (nth a-nodes 2) false)
        (g/edge! graph (nth a-nodes 2) (nth a-nodes 3) false)
        (g/edge! graph (nth a-nodes 3) (nth a-nodes 4) false)
        (g/edge! graph (nth a-nodes 4) (nth a-nodes 0) false))

      ;; Community 2
      (doseq [i (range 5)]
        (g/node! graph (str "b" i) (str "Beta " i)))
      (let [b-nodes (mapv #(g/node-by-id graph (str "b" %)) (range 5))]
        (g/edge! graph (nth b-nodes 0) (nth b-nodes 1) false)
        (g/edge! graph (nth b-nodes 1) (nth b-nodes 2) false)
        (g/edge! graph (nth b-nodes 2) (nth b-nodes 3) false)
        (g/edge! graph (nth b-nodes 3) (nth b-nodes 4) false)
        (g/edge! graph (nth b-nodes 4) (nth b-nodes 0) false))

      ;; Bridge between communities
      (g/edge! graph (g/node-by-id graph "a0") (g/node-by-id graph "b0") false)

      ;; Run statistics
      (stat/degree! gm)
      (stat/pagerank! gm false)
      (stat/modularity! gm)

      ;; Apply appearance
      (let [am (app/appearance-model)]
        (app/color-by-modularity! am graph gm)
        (app/size-by-degree! graph am gm 10 30)
        (is (not (nil? am)))))))
