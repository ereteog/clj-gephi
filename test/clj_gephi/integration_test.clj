(ns clj-gephi.integration-test
  (:require [clj-gephi.graph :as g]
            [clj-gephi.project :as p]
            [clj-gephi.statistics :as stat]
            [clj-gephi.filters :as f]
            [clj-gephi.layout :as lay]
            [clj-gephi.appearance :as app]
            [clj-gephi.io.import :as imp]
            [clj-gephi.io.export :as exp]
            [clojure.test :refer :all]
            [clojure.java.io :as io])
  (:import [java.awt Color]))

(defn create-collaboration-network
  "Creates a realistic collaboration network representing researchers"
  [gm]
  (let [graph (g/undirected-graph gm)
        ;; Research Group 1: AI researchers (well-connected)
        alice (g/node! graph "alice" "Alice Chen")
        bob (g/node! graph "bob" "Bob Smith")
        charlie (g/node! graph "charlie" "Charlie Johnson")
        diana (g/node! graph "diana" "Diana Martinez")

        ;; Research Group 2: Systems researchers (well-connected)
        eve (g/node! graph "eve" "Eve Williams")
        frank (g/node! graph "frank" "Frank Brown")
        grace (g/node! graph "grace" "Grace Lee")
        henry (g/node! graph "henry" "Henry Davis")

        ;; Research Group 3: Theory researchers (smaller group)
        iris (g/node! graph "iris" "Iris Wilson")
        jack (g/node! graph "jack" "Jack Moore")

        ;; Bridge researchers (collaborate across groups)
        karen (g/node! graph "karen" "Karen Taylor")

        ;; Peripheral researchers (few collaborations)
        leo (g/node! graph "leo" "Leo Anderson")
        maria (g/node! graph "maria" "Maria Garcia")]

    ;; AI Research Group collaborations (dense)
    (g/edge! graph alice bob false)
    (g/edge! graph alice charlie false)
    (g/edge! graph alice diana false)
    (g/edge! graph bob charlie false)
    (g/edge! graph bob diana false)
    (g/edge! graph charlie diana false)

    ;; Systems Research Group collaborations (dense)
    (g/edge! graph eve frank false)
    (g/edge! graph eve grace false)
    (g/edge! graph eve henry false)
    (g/edge! graph frank grace false)
    (g/edge! graph frank henry false)
    (g/edge! graph grace henry false)

    ;; Theory Research Group collaborations
    (g/edge! graph iris jack false)

    ;; Bridge researcher connections
    (g/edge! graph karen alice false)
    (g/edge! graph karen bob false)
    (g/edge! graph karen eve false)
    (g/edge! graph karen frank false)
    (g/edge! graph karen iris false)

    ;; Peripheral researchers
    (g/edge! graph leo alice false)
    (g/edge! graph maria eve false)

    graph))

(deftest test-complete-network-analysis-workflow
  (testing "Complete network analysis workflow from creation to export"
    (let [;; Step 1: Create project and workspace
          _ (p/new-project!)
          wp (p/current-workspace)
          gm (g/graph-model wp)

          ;; Step 2: Create network
          graph (create-collaboration-network gm)]

      ;; Step 3: Verify initial structure
      (is (= 13 (g/node-count graph)))
      (is (= 19 (g/edge-count graph)))

      ;; Step 4: Calculate network statistics
      (let [degree-stat (stat/degree! gm)
            pagerank-stat (stat/pagerank! gm false)
            distance-stat (stat/distance! gm false)
            modularity-stat (stat/modularity! gm)]

        ;; Verify statistics were calculated
        (is (not (nil? degree-stat)))
        (is (not (nil? pagerank-stat)))
        (is (not (nil? distance-stat)))
        (is (not (nil? modularity-stat)))

        ;; Check some metrics
        (is (> (stat/average-degree degree-stat) 2.0))
        (is (< (stat/average-degree degree-stat) 4.0))
        (is (number? (stat/diameter distance-stat)))
        (is (number? (stat/avg-distance distance-stat))))

      ;; Step 5: Apply filters
      (f/filter-by-degree! gm (f/visible-view gm) 2)
      (let [filtered-graph (g/visible-graph gm)
            filtered-count (g/node-count filtered-graph)]
        ;; Filtered graph should have fewer nodes
        (is (< filtered-count 13))
        ;; But more than half should remain (central researchers)
        (is (> filtered-count 6)))

      ;; Step 6: Reset to full graph for layout
      (f/filter-by-degree! gm (f/visible-view gm) 1)

      ;; Step 7: Apply layout algorithms
      (let [force-opts {:attraction-strength 1.5
                        :repulsion-strength 0.5}
            yifan-opts {:step-displacement 1.0
                        :optimal-distance 200.0}
            noverlap-opts {:margin 5.0
                           :ratio 1.5}]
        ;; Apply multi-stage layout
        (lay/force-atlas! gm 20 force-opts)
        (lay/yifan-hu! gm 20 yifan-opts)
        (lay/noverlap! gm 20 noverlap-opts))

      ;; Step 8: Apply appearance (colors and sizes)
      (let [am (app/appearance-model)
            visible-graph (g/visible-graph gm)]
        ;; Color by modularity (community detection)
        (app/color-by-modularity! am visible-graph gm)
        ;; Size by PageRank
        (app/size-by-pagerank! visible-graph am gm 5 25))

      ;; Step 9: Export to file
      (let [export-file "test/resources/integration-test-output.gexf"]
        (when (.exists (io/file export-file))
          (io/delete-file export-file))

        (exp/export-graph-file! export-file "gexf" wp true)

        ;; Verify export was successful
        (is (.exists (io/file export-file)))

        ;; Clean up
        (when (.exists (io/file export-file))
          (io/delete-file export-file))))))

(deftest test-import-analyze-export-workflow
  (testing "Import existing network, analyze, and export"
    (let [test-file "test/resources/sample-network.gexf"
          export-file "test/resources/analyzed-output.gexf"]
      (when (.exists (io/file test-file))
        ;; Step 1: Import network
        (p/new-project!)
        (let [wp (p/current-workspace)
              gm (g/graph-model wp)]
          (imp/import-graph-file! wp test-file false)

          (let [graph (g/undirected-graph gm)
                original-nodes (g/node-count graph)
                original-edges (g/edge-count graph)]

            ;; Step 2: Run comprehensive analysis
            (stat/degree! gm)
            (stat/pagerank! gm false)
            (stat/distance! gm false)
            (stat/modularity! gm)

            ;; Step 3: Apply layout
            (let [opts {:attraction-strength 1.5
                        :repulsion-strength 0.5}]
              (lay/force-atlas! gm 30 opts))

            ;; Step 4: Style the network
            (let [am (app/appearance-model)]
              (app/color-by-modularity! am graph gm)
              (app/size-by-degree! graph am gm 8 20))

            ;; Step 5: Export analyzed network
            (when (.exists (io/file export-file))
              (io/delete-file export-file))
            (exp/export-graph-file! export-file "gexf" wp true)

            ;; Verify
            (is (.exists (io/file export-file)))

            ;; Clean up
            (when (.exists (io/file export-file))
              (io/delete-file export-file))))))))

(deftest test-multi-workspace-workflow
  (testing "Working with multiple workspaces"
    (let [;; Create project
          proj (p/new-project!)
          wp1 (p/current-workspace)
          gm1 (g/graph-model wp1)

          ;; Create first network
          graph1 (g/undirected-graph gm1)]

      ;; Add nodes to first workspace
      (doseq [i (range 5)]
        (g/node! graph1 (str "n" i) (str "Node " i)))

      (is (= 5 (g/node-count graph1)))

      ;; Create second workspace
      (let [wp2 (p/new-workspace! proj)
            _ (p/open-workspace! wp2)
            gm2 (g/graph-model wp2)
            graph2 (g/undirected-graph gm2)]

        ;; Add nodes to second workspace
        (doseq [i (range 10)]
          (g/node! graph2 (str "m" i) (str "Vertex " i)))

        (is (= 10 (g/node-count graph2)))

        ;; Switch back to first workspace
        (p/open-workspace! wp1)
        (let [current-graph (g/undirected-graph gm1)]
          ;; First workspace should still have 5 nodes
          (is (= 5 (g/node-count current-graph))))))))

(deftest test-filter-analyze-export-pipeline
  (testing "Pipeline: create -> filter -> analyze -> export"
    (let [_ (p/new-project!)
          wp (p/current-workspace)
          gm (g/graph-model wp)
          graph (create-collaboration-network gm)
          export-file "test/resources/filtered-network.gexf"]

      ;; Calculate statistics first
      (stat/degree! gm)
      (stat/pagerank! gm false)

      ;; Filter to keep only well-connected researchers
      (f/filter-by-degree! gm (f/visible-view gm) 3)

      (let [filtered-graph (g/visible-graph gm)]
        ;; Only researchers with 3+ collaborations
        (is (< (g/node-count filtered-graph) 13))

        ;; Apply layout to filtered graph
        (let [opts {:attraction-strength 2.0
                    :repulsion-strength 0.5}]
          (lay/force-atlas! gm 25 opts))

        ;; Style filtered graph
        (let [am (app/appearance-model)]
          (app/size-by-pagerank! filtered-graph am gm 10 30))

        ;; Export only visible (filtered) graph
        (when (.exists (io/file export-file))
          (io/delete-file export-file))
        (exp/export-graph-file! export-file "gexf" wp true)

        (is (.exists (io/file export-file)))

        ;; Clean up
        (when (.exists (io/file export-file))
          (io/delete-file export-file))))))

(deftest test-network-metrics-comprehensive
  (testing "Comprehensive network metrics analysis"
    (let [_ (p/new-project!)
          wp (p/current-workspace)
          gm (g/graph-model wp)
          graph (create-collaboration-network gm)]

      ;; Calculate all available metrics
      (let [degree-stat (stat/degree! gm)
            pagerank-stat (stat/pagerank! gm false)
            distance-stat (stat/distance! gm false)
            modularity-stat (stat/modularity! gm)]

        ;; Verify degree statistics
        (let [avg-degree (stat/average-degree degree-stat)]
          (is (number? avg-degree))
          (is (> avg-degree 0)))

        ;; Verify distance statistics
        (let [diameter (stat/diameter distance-stat)
              avg-dist (stat/avg-distance distance-stat)
              radius (stat/radius distance-stat)]
          (is (number? diameter))
          (is (number? avg-dist))
          (is (number? radius))
          (is (>= diameter radius))
          (is (> avg-dist 0)))

        ;; Verify columns were created
        (is (not (nil? (stat/column gm stat/degree-idx))))
        (is (not (nil? (stat/column gm stat/pagerank-idx))))
        (is (not (nil? (stat/column gm stat/betweenness-idx))))
        (is (not (nil? (stat/column gm stat/modularity-idx))))

        ;; Generate HTML reports
        (let [degree-report (stat/html-report degree-stat)
              distance-report (stat/html-report distance-stat)
              pagerank-report (stat/html-report pagerank-stat)
              modularity-report (stat/html-report modularity-stat)]
          (is (string? degree-report))
          (is (string? distance-report))
          (is (string? pagerank-report))
          (is (string? modularity-report))
          (is (> (count degree-report) 0))
          (is (> (count distance-report) 0)))))))
