(ns clj-gephi.statistics
  (import [org.gephi.statistics.plugin Modularity])
  (import [org.gephi.statistics.plugin GraphDistance]))

(def betweenness-idx GraphDistance/BETWEENNESS)
(def modularity-idx Modularity/MODULARITY_CLASS)

(defn column
  [gm col-idx]
  (-> (.getNodeTable gm)
      (.getColumn col-idx)))

(defn distance!
  "GraphModel -> GraphDistance"
  [gm directed?]
  (let [gd (GraphDistance.)]
    (.setDirected gd directed?)
    (.execute gd gm)
    gd))

(defn modularity!
  "GraphModel -> Modularity"
  [gm]
  (let [modularity (Modularity.)]
    (.execute modularity gm)
    modularity))

(defn diameter
  "GraphDistance -> double"
  [gd]
  (.getDiameter gd)
  )

(defn avg-distance
  "GraphDistance -> double"
  [gd]
  (.getPathLength gd))

(defn radius
  "GraphDistance -> double"
  [gd]
  (.getRadius gd))

(defn html-report
  [gd]
  (.getReport gd))
