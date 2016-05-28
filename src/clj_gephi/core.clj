(ns clj-gephi.core
  [require [clojure.java.io :as io]]
  [import [org.gephi.project.api ProjectController Workspace]]
  [import [org.gephi.graph.api GraphController GraphModel]]
  [import [org.gephi.filters.api FilterController]]
  [import [org.gephi.preview.api PreviewController]]
  [import [org.gephi.appearance.api AppearanceController]]
  [import [org.gephi.io.importer.api Container ImportController EdgeDirectionDefault]]
  [import [org.openide.util Lookup]]
  [import [org.gephi.io.processor.plugin DefaultProcessor]])

(defn lookup [c]
  (.lookup (Lookup/getDefault) c))

(defn project-controller []
  (lookup ProjectController))

(defn new-project!
  "pc :: ProjectController
  add a new project to pc"
  [pc]
   (.newProject pc))

(defn workspace
  [project-controller]
  (.getCurrentWorkspace project-controller))

(defn graph-controller []
  (lookup GraphController))

(defn graph-model
  "GraphController -> GraphModel"
  [gc]
  (.getGraphModel gc))

(defn directed-graph
  "GraphModel -> DirectedGraph"
  [gm]
  (.getDirectedGraph gm))

(defn node-count
  "Graph -> Integer"
  [g]
  (.getNodeCount g))

(defn edge-count
  "Graph -> Integer"
  [g]
  (.getEdgeCount g))

(defn preview-controller []
  (lookup PreviewController))

(defn preview-model
  "PreviewController -> PreviewModel"
  [pc]
  (.getModel pc))

(defn import-controller []
  (lookup ImportController))

(defn filter-controller []
  (lookup FilterController))

(defn appearance-controller []
  (lookup AppearanceController))


(defn import-graph-file!
  "Workspace -> ImportController -> String -> EdgeDirectionDefault -> Container
  import graph file into workspace wp"
  [wp ic file-path directed?]
  (let [container (.importFile ic (io/file file-path))]
    (when directed?
      (.setEdgeDefault (.getLoader container) (EdgeDirectionDefault/DIRECTED)))
    (.process ic container (DefaultProcessor.) wp)))

(defn test-gephi
  "https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/HeadlessSimple.java"
  []
  ;; Init a project - and therefore a workspace
  (def pc (project-controller))
  (new-project! pc)
  (def wp (workspace pc))
  (def ic (import-controller))
  (def graphModel (graph-model (graph-controller)))
  (import-graph-file! wp ic "resources/netowrk-twitter-sarko-20130706.gexf" true)

  ;; print graph stats
  (def graph (directed-graph graphModel))
  (println "Nodes: " (node-count graph))
  (println "Edges: " (edge-count graph))

  )
