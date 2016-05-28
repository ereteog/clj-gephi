(ns clj-gephi.graph
  (import [org.gephi.graph.api GraphController GraphModel])
  (import [org.openide.util Lookup]))

(def gc
  (.lookup (Lookup/getDefault) GraphController))

(defn graph-model
  "GraphModel"
  []
  (.getGraphModel gc))

(defn directed-graph
  "GraphModel -> DirectedGraph"
  [gm]
  (.getDirectedGraph gm))

(defn visible-graph
  "GraphModel -> Graph"
  [gm]
  (.getGraphVisible gm))

(defn node-count
  "Graph -> Integer"
  [g]
  (.getNodeCount g))

(defn edge-count
  "Graph -> Integer"
  [g]
  (.getEdgeCount g))

