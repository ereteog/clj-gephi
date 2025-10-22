(ns clj-gephi.layout-test
  (:require [clj-gephi.graph :as g]
            [clj-gephi.layout :as lay]
            [clojure.test :refer :all]
            [clj-gephi.project :as p]))

(defn create-test-workspace []
  (p/new-project!)
  (p/current-workspace))

(defn create-simple-network
  "Creates a simple network for layout testing"
  [gm]
  (let [graph (g/undirected-graph gm)]
    (doseq [i (range 6)]
      (g/node! graph (str "n" i) (str "Node " i)))
    ;; Create edges to form a connected graph
    (let [nodes (mapv #(g/node-by-id graph (str "n" %)) (range 6))]
      (g/edge! graph (nth nodes 0) (nth nodes 1) false)
      (g/edge! graph (nth nodes 1) (nth nodes 2) false)
      (g/edge! graph (nth nodes 2) (nth nodes 3) false)
      (g/edge! graph (nth nodes 3) (nth nodes 4) false)
      (g/edge! graph (nth nodes 4) (nth nodes 5) false)
      (g/edge! graph (nth nodes 5) (nth nodes 0) false)
      (g/edge! graph (nth nodes 0) (nth nodes 3) false))
    graph))

(deftest test-yifan-hu-layout-creation
  (testing "Creating YifanHu layout with options"
    (let [opts {:step-displacement 1.0
                :optimal-distance 200.0}
          layout (lay/yifan-hu opts)]
      (is (not (nil? layout))))))

(deftest test-yifan-hu-layout-execution
  (testing "Executing YifanHu layout on graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-simple-network gm)
          opts {:step-displacement 1.0
                :optimal-distance 100.0}
          layout (lay/yifan-hu! gm 10 opts)]
      (is (not (nil? layout)))
      ;; After layout, nodes should have positions
      (let [nodes (g/all-nodes graph)]
        (is (> (.size nodes) 0))))))

(deftest test-force-atlas-layout-creation
  (testing "Creating ForceAtlas layout with options"
    (let [opts {:attraction-strength 1.5
                :repulsion-strength 0.5}
          layout (lay/force-atlas opts)]
      (is (not (nil? layout))))))

(deftest test-force-atlas-layout-execution
  (testing "Executing ForceAtlas layout on graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-simple-network gm)
          opts {:attraction-strength 1.5
                :repulsion-strength 0.5
                :gravity 1.0}
          layout (lay/force-atlas! gm 10 opts)]
      (is (not (nil? layout))))))

(deftest test-noverlap-layout-creation
  (testing "Creating Noverlap layout with options"
    (let [opts {:margin 5.0
                :ratio 1.5}
          layout (lay/noverlap opts)]
      (is (not (nil? layout))))))

(deftest test-noverlap-layout-execution
  (testing "Executing Noverlap layout on graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-simple-network gm)
          opts {:margin 10.0
                :ratio 2.0}
          layout (lay/noverlap! gm 10 opts)]
      (is (not (nil? layout))))))

(deftest test-label-adjust-layout-creation
  (testing "Creating LabelAdjust layout with options"
    (let [opts {:adjust-by-size? true
                :speed 1.0}
          layout (lay/label-adjust opts)]
      (is (not (nil? layout))))))

(deftest test-label-adjust-layout-execution
  (testing "Executing LabelAdjust layout on graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-simple-network gm)
          opts {:adjust-by-size? true}
          layout (lay/label-adjust! gm 10 opts)]
      (is (not (nil? layout))))))

(deftest test-sequential-layouts
  (testing "Applying multiple layouts sequentially"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-simple-network gm)
          force-opts {:attraction-strength 1.5
                      :repulsion-strength 0.5}
          yifan-opts {:step-displacement 1.0
                      :optimal-distance 150.0}
          noverlap-opts {:margin 5.0
                         :ratio 1.5}]
      ;; Apply Force Atlas
      (lay/force-atlas! gm 10 force-opts)
      ;; Apply Yifan Hu
      (lay/yifan-hu! gm 10 yifan-opts)
      ;; Apply Noverlap to prevent overlaps
      (let [layout (lay/noverlap! gm 10 noverlap-opts)]
        (is (not (nil? layout)))))))

(deftest test-layout-with-different-iterations
  (testing "Running layout with different iteration counts"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-simple-network gm)
          opts {:attraction-strength 1.0
                :repulsion-strength 1.0}]
      ;; Test with 1 iteration
      (let [layout1 (lay/force-atlas! gm 1 opts)]
        (is (not (nil? layout1))))
      ;; Test with 50 iterations
      (let [layout50 (lay/force-atlas! gm 50 opts)]
        (is (not (nil? layout50))))
      ;; Test with 100 iterations
      (let [layout100 (lay/force-atlas! gm 100 opts)]
        (is (not (nil? layout100)))))))

(deftest test-yifan-hu-all-options
  (testing "YifanHu layout with all optional parameters"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-simple-network gm)
          opts {:step-displacement 1.0
                :optimal-distance 200.0
                :relative-strength 0.2
                :step-ratio 0.95}
          layout (lay/yifan-hu! gm 20 opts)]
      (is (not (nil? layout))))))

(deftest test-force-atlas-all-options
  (testing "ForceAtlas layout with all optional parameters"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-simple-network gm)
          opts {:adjust-sizes? true
                :attraction-strength 2.0
                :repulsion-strength 1.0
                :gravity 5.0
                :speed 1.0
                :cooling 1.0
                :inertia 0.1}
          layout (lay/force-atlas! gm 20 opts)]
      (is (not (nil? layout))))))

(deftest test-layout-on-larger-network
  (testing "Running layout on a larger network"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (g/undirected-graph gm)]
      ;; Create a network with 20 nodes
      (doseq [i (range 20)]
        (g/node! graph (str "n" i) (str "Node " i)))
      ;; Create random edges
      (let [nodes (mapv #(g/node-by-id graph (str "n" %)) (range 20))]
        (doseq [i (range 20)]
          (let [j (mod (+ i 1) 20)
                k (mod (+ i 5) 20)]
            (g/edge! graph (nth nodes i) (nth nodes j) false)
            (when (not= i k)
              (g/edge! graph (nth nodes i) (nth nodes k) false)))))

      ;; Apply layout
      (let [opts {:attraction-strength 1.5
                  :repulsion-strength 0.5}
            layout (lay/force-atlas! gm 30 opts)]
        (is (not (nil? layout)))
        (is (= 20 (g/node-count graph)))))))

(deftest test-realistic-layout-pipeline
  (testing "Realistic layout pipeline with multiple stages"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-simple-network gm)
          ;; Stage 1: Initial layout with Force Atlas
          force-opts {:attraction-strength 1.5
                      :repulsion-strength 0.5}
          ;; Stage 2: Refinement with Yifan Hu
          yifan-opts {:step-displacement 1.0
                      :optimal-distance 200.0}
          ;; Stage 3: Prevent overlaps
          noverlap-opts {:margin 10.0
                         :ratio 2.0}
          ;; Stage 4: Adjust labels
          label-opts {:adjust-by-size? true}]

      ;; Execute pipeline
      (lay/force-atlas! gm 50 force-opts)
      (lay/yifan-hu! gm 50 yifan-opts)
      (lay/noverlap! gm 50 noverlap-opts)
      (let [final-layout (lay/label-adjust! gm 100 label-opts)]
        (is (not (nil? final-layout)))))))
