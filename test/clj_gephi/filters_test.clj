(ns clj-gephi.filters-test
  (:require [clj-gephi.graph :as g]
            [clj-gephi.filters :as f]
            [clj-gephi.statistics :as stat]
            [clojure.test :refer :all]
            [clj-gephi.project :as p]))

(defn create-test-workspace []
  (p/new-project!)
  (p/current-workspace))

(defn create-network-with-varying-degrees
  "Creates a network where nodes have different degrees"
  [gm]
  (let [graph (g/undirected-graph gm)
        ;; Create a hub with high degree
        hub (g/node! graph "hub" "Hub")
        ;; Create medium-degree nodes
        m1 (g/node! graph "m1" "Medium 1")
        m2 (g/node! graph "m2" "Medium 2")
        m3 (g/node! graph "m3" "Medium 3")
        ;; Create low-degree nodes
        l1 (g/node! graph "l1" "Low 1")
        l2 (g/node! graph "l2" "Low 2")
        l3 (g/node! graph "l3" "Low 3")
        l4 (g/node! graph "l4" "Low 4")]
    ;; Hub connects to all medium nodes (degree 3)
    (g/edge! graph hub m1 false)
    (g/edge! graph hub m2 false)
    (g/edge! graph hub m3 false)

    ;; Medium nodes connect to each other (degrees 3-4)
    (g/edge! graph m1 m2 false)
    (g/edge! graph m2 m3 false)

    ;; Medium nodes connect to low-degree nodes
    (g/edge! graph m1 l1 false)
    (g/edge! graph m2 l2 false)
    (g/edge! graph m3 l3 false)
    (g/edge! graph m3 l4 false)

    ;; Calculate degrees
    (stat/degree! gm)
    graph))

(deftest test-visible-view
  (testing "Getting visible view from graph model"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-network-with-varying-degrees gm)
          view (f/visible-view gm)]
      (is (not (nil? view))))))

(deftest test-filter-by-minimum-degree
  (testing "Filtering nodes by minimum degree"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-network-with-varying-degrees gm)
          original-count (g/node-count graph)]
      ;; Filter to show only nodes with degree >= 2
      (f/filter-by-degree! gm (f/visible-view gm) 2)
      (let [visible-graph (g/visible-graph gm)
            filtered-count (g/node-count visible-graph)]
        (is (< filtered-count original-count))
        ;; All low-degree nodes (degree 1) should be filtered out
        (is (>= filtered-count 1))))))

(deftest test-filter-by-degree-range
  (testing "Filtering nodes by degree range"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-network-with-varying-degrees gm)
          original-count (g/node-count graph)]
      ;; Filter to show only nodes with degree 2-3
      (f/filter-by-degree! gm (f/visible-view gm) 2 3)
      (let [visible-graph (g/visible-graph gm)
            filtered-count (g/node-count visible-graph)]
        (is (< filtered-count original-count))
        ;; Should exclude both very low degree and very high degree nodes
        (is (>= filtered-count 1))))))

(deftest test-filter-preserves-original-graph
  (testing "Filtering creates view without modifying original graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-network-with-varying-degrees gm)
          original-node-count (g/node-count graph)
          original-edge-count (g/edge-count graph)]
      ;; Apply filter
      (f/filter-by-degree! gm (f/visible-view gm) 3)
      ;; Original graph should be unchanged
      (is (= original-node-count (g/node-count graph)))
      (is (= original-edge-count (g/edge-count graph)))
      ;; But visible graph should be different
      (let [visible-graph (g/visible-graph gm)]
        (is (< (g/node-count visible-graph) original-node-count))))))

(deftest test-view-by-degree
  (testing "Creating a degree-filtered view"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-network-with-varying-degrees gm)
          view (f/view-by-degree gm (f/visible-view gm) 2)]
      (is (not (nil? view))))))

(deftest test-set-visible-view
  (testing "Setting visible view on graph model"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-network-with-varying-degrees gm)
          new-view (f/view-by-degree gm (f/visible-view gm) 2)
          result (f/set-visible-view! gm new-view)]
      (is (not (nil? result)))
      (is (= gm result)))))

(deftest test-sequential-filters
  (testing "Applying multiple filters sequentially"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-network-with-varying-degrees gm)
          original-count (g/node-count graph)]
      ;; First filter: degree >= 1
      (f/filter-by-degree! gm (f/visible-view gm) 1)
      (let [first-filtered (g/node-count (g/visible-graph gm))]
        ;; Second filter: degree >= 2 (more restrictive)
        (f/filter-by-degree! gm (f/visible-view gm) 2)
        (let [second-filtered (g/node-count (g/visible-graph gm))]
          (is (<= second-filtered first-filtered))
          (is (<= second-filtered original-count)))))))

(deftest test-filter-on-star-network
  (testing "Filtering a star network by degree"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/undirected-graph gm)
          center (g/node! graph "center" "Center")]
      ;; Create star with 10 peripheral nodes
      (doseq [i (range 10)]
        (let [node (g/node! graph (str "node-" i) (str "Node " i))]
          (g/edge! graph center node false)))

      (stat/degree! gm)
      (is (= 11 (g/node-count graph)))

      ;; Filter to show only high-degree nodes (>= 5)
      (f/filter-by-degree! gm (f/visible-view gm) 5)
      (let [visible-graph (g/visible-graph gm)]
        ;; Only the center node has degree >= 5
        (is (= 1 (g/node-count visible-graph)))))))
