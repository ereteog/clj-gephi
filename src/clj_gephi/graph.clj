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
   (or (node-by-id g id)
       (node (.getModel g) id)))
  ([g id label]
   (or (node-by-id g id)
       (let [created (node (.getModel g) id)]
         (.setLabel created label)
         created))))

(defn add-node! [g n]
  (.addNode g n)
  n)

(defn node! [g & args]
  "Graph -> Node"
  (->> (apply node g args)
       (add-node! g)))

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
  [g & args] (apply edge (.getModel g) args))

(defn add-edge!
  [g e] (.addEdge g e) e)

(defn edge! [g & args]
  "Graph -> Edge"
  (->> (apply edge g args)
       (add-edge! g)))

(defn source
  "Edge -> Node"
  [edge]
  (.getSource edge))

(defn target
  "Edge -> Node"
  [edge]
  (.getTarget edge))

(defn all-nodes [g]
  (.toCollection (.getNodes g)))

(defn all-edges
  ([g]       (.toCollection (.getEdges g)))
  ([g n1]    (.toCollection (.getEdges g n1)))
  ([g n1 n2] (.toCollection (.getEdges g n1 n2))))

(defn clear-nodes
  "Graph -> Graph"
  [g]
  (.removeAllNodes g (all-nodes g)))
