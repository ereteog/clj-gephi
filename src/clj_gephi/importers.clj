(ns clj-gephi.importers
  (require [clojure.java.io :as io])
  (import [org.gephi.io.processor.plugin DefaultProcessor])
  (import [org.gephi.io.importer.api Container ImportController EdgeDirectionDefault])
  (import [org.openide.util Lookup]))

(def ic
  (.lookup (Lookup/getDefault)  ImportController))

(defn import-graph-file!
  "Workspace -> String -> EdgeDirectionDefault -> Container
  import graph file into workspace wp"
  [wp file-path directed?]
  (let [container (.importFile ic (io/file file-path))]
    (when directed?
      (.setEdgeDefault (.getLoader container) (EdgeDirectionDefault/DIRECTED)))
    (.process ic container (DefaultProcessor.) wp)))

