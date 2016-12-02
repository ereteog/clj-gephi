(ns clj-gephi.graph
  (import [org.gephi.graph.api GraphController GraphModel Graph])
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

(defn has-node?
  [g node-id]
  (.hasNode g node-id))

(defn node-by-id
  [g node-id]
  (.getNode g node-id))

(defmulti node
  "(GraphModel | Graph) -> Node"
  (fn [x & args] (class x)))
(defmethod node GraphModel
  ([gm]
   (-> (.factory gm)
       (.newNode)))
  ([gm node-id]
   (-> (.factory gm)
       (.newNode node-id)))
  ([gm node-id label]
   (doto (node gm node-id)
     (.setLabel label))))
(defmethod node Graph
  ([g] (node (.getModel g)))
  ([g id]
   (or (node-by-id id)
       (apply node (.getModel g) id)))
  ([g id label]
   (-> (node g id) (.setLabel label))))

(defmulti edge
  "https://gephi.org/gephi/0.9.1/apidocs/org/gephi/graph/api/GraphFactory.html"
  (fn [x & args] (class x)))
(defmethod edge GraphModel
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
(defmethod edge Graph
  [gm & args] (apply edge (.getModel g) args))

(defn source
  "Edge -> Node"
  [edge]
  (.getSource edge))

(defn target
  "Edge -> Node"
  [edge]
  (.getTarget edge))

(defn add-node! [g node] (.addNode g node))

(defn add-edge!
  ([g edge] (.addEdge g edge) g)
  ([g edge add-nodes?]
   (when add-nodes?
     (->> edge source (add-node! g))
     (->> edge target (add-node! g)))
   (add-edge! g edge)))

