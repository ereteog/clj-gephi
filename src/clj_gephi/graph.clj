(ns clj-gephi.graph
  (import [org.gephi.graph.api GraphController GraphModel])
  (import [org.openide.util Lookup]))

(def gc
  (.lookup (Lookup/getDefault) GraphController))

(defn graph-model
  "GraphModel"
  ([] (.getGraphModel gc))
  ([wp] (.getGraphModel gc wp)))

(defn undirected-graph
  "GraphModel -> UndirectedGraph"
  [gm]
  (.getUndirectedGraph gm))

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

(defn node
  "GraphModel -> Node
https://gephi.org/gephi/0.9.1/apidocs/org/gephi/graph/api/GraphFactory.html
  "
  ([gm]
   (-> (.factory gm)
       (.newNode)))
  ([gm node-id]
   (-> (.factory gm)
       (.newNode node-id)))
  ([gm node-id label]
   (doto (node gm node-id)
     (.setLabel label))))

(defn edge
  "GraphModel -> Node -> Node -> Edge
  https://gephi.org/gephi/0.9.1/apidocs/org/gephi/graph/api/GraphFactory.html"
  ([gm node1 node2]
   (-> (.factory gm)
       (.newEdge node1 node2)))
  ([gm node1 node2 directed?]
   (-> (.factory gm)
       (.newEdge node1 node2 directed?)))
  ([gm node1 node2 directed? edge-type]
   (-> (.factory gm)
       (.newEdge node1 node2 edge-type directed?)))
  ([gm node1 node2 directed? edge-type weight]
   (-> (.factory gm)
       (.newEdge node1 node2 edge-type weight directed?)))
  ([gm node1 node2 directed? edge-type weight id]
   (-> (.factory gm)
       (.newEdge id node1 node2 edge-type weight directed?))))

(defn source
  "Edge -> Node"
  [edge]
  (.getSource edge))

(defn target
  "Edge -> Node"
  [edge]
  (.getTarget edge))

(defn add-node! [g node] (.addNode g node) g)
(defn add-edge!
  ([g edge] (.addEdge g edge) g)
  ([g edge add-nodes?]
   (when add-nodes?
     (->> edge source (add-node! g))
     (->> edge target (add-node! g)))
   (add-edge! g edge)))

