(ns clj-gephi.io-test
  (:require [clj-gephi.graph :as g]
            [clj-gephi.io.import :as imp]
            [clj-gephi.io.export :as exp]
            [clojure.test :refer :all]
            [clj-gephi.project :as p]
            [clojure.java.io :as io]))

(defn create-test-workspace []
  (p/new-project!)
  (p/current-workspace))

(defn create-sample-network
  "Creates a sample network for export testing"
  [gm]
  (let [graph (g/undirected-graph gm)
        alice (g/node! graph "alice" "Alice")
        bob (g/node! graph "bob" "Bob")
        charlie (g/node! graph "charlie" "Charlie")]
    (g/edge! graph alice bob false)
    (g/edge! graph alice charlie false)
    (g/edge! graph bob charlie false)
    graph))

(deftest test-import-gexf-file
  (testing "Importing a GEXF file"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          test-file "test/resources/sample-network.gexf"]
      ;; Check if test file exists
      (when (.exists (io/file test-file))
        (imp/import-graph-file! wp test-file false)
        (let [graph (g/undirected-graph gm)]
          ;; Sample network has 5 nodes
          (is (= 5 (g/node-count graph)))
          ;; Sample network has 6 edges
          (is (= 6 (g/edge-count graph)))
          ;; Check specific nodes exist
          (is (g/has-node? graph "alice"))
          (is (g/has-node? graph "bob"))
          (is (g/has-node? graph "charlie"))
          (is (g/has-node? graph "diana"))
          (is (g/has-node? graph "eve")))))))

(deftest test-import-as-directed
  (testing "Importing a network as directed"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          test-file "test/resources/sample-network.gexf"]
      (when (.exists (io/file test-file))
        (imp/import-graph-file! wp test-file true)
        (let [graph (g/directed-graph gm)]
          (is (= 5 (g/node-count graph)))
          (is (> (g/edge-count graph) 0)))))))

(deftest test-export-gexf-file
  (testing "Exporting graph to GEXF format"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-sample-network gm)
          output-file "test/resources/exported-network.gexf"]
      ;; Delete output file if it exists
      (when (.exists (io/file output-file))
        (io/delete-file output-file))

      ;; Export the graph
      (exp/export-graph-file! output-file "gexf" wp true)

      ;; Check that file was created
      (is (.exists (io/file output-file)))

      ;; Clean up
      (when (.exists (io/file output-file))
        (io/delete-file output-file)))))

(deftest test-export-with-workspace
  (testing "Exporting graph with specific workspace"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-sample-network gm)
          output-file "test/resources/exported-with-workspace.gexf"]
      ;; Delete output file if it exists
      (when (.exists (io/file output-file))
        (io/delete-file output-file))

      ;; Export using workspace parameter
      (exp/export-graph-file! output-file wp)

      ;; Check that file was created
      (is (.exists (io/file output-file)))

      ;; Clean up
      (when (.exists (io/file output-file))
        (io/delete-file output-file)))))

(deftest test-gexf-exporter-creation
  (testing "Creating GEXF exporter"
    (let [wp (create-test-workspace)
          exporter (exp/gexf-exporter wp true)]
      (is (not (nil? exporter))))))

(deftest test-import-export-roundtrip
  (testing "Import-export roundtrip preserves graph structure"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          test-file "test/resources/sample-network.gexf"
          export-file "test/resources/roundtrip-export.gexf"]
      (when (.exists (io/file test-file))
        ;; Import original file
        (imp/import-graph-file! wp test-file false)
        (let [graph (g/undirected-graph gm)
              original-nodes (g/node-count graph)
              original-edges (g/edge-count graph)]

          ;; Export to new file
          (when (.exists (io/file export-file))
            (io/delete-file export-file))
          (exp/export-graph-file! export-file "gexf" wp true)

          ;; Import the exported file into a new workspace
          (p/new-project!)
          (let [wp2 (p/current-workspace)
                gm2 (g/graph-model wp2)]
            (imp/import-graph-file! wp2 export-file false)
            (let [graph2 (g/undirected-graph gm2)
                  reimported-nodes (g/node-count graph2)
                  reimported-edges (g/edge-count graph2)]

              ;; Node and edge counts should match
              (is (= original-nodes reimported-nodes))
              (is (= original-edges reimported-edges))))

          ;; Clean up
          (when (.exists (io/file export-file))
            (io/delete-file export-file)))))))

(deftest test-export-visible-only
  (testing "Exporting only visible graph"
    (let [wp (create-test-workspace)
          gm (g/graph-model wp)
          graph (create-sample-network gm)
          output-file "test/resources/exported-visible.gexf"]
      ;; Delete output file if it exists
      (when (.exists (io/file output-file))
        (io/delete-file output-file))

      ;; Export only visible graph
      (exp/export-graph-file! output-file "gexf" wp true)

      ;; Check that file was created
      (is (.exists (io/file output-file)))

      ;; Clean up
      (when (.exists (io/file output-file))
        (io/delete-file output-file)))))
