(ns clj-gephi.statistics
  (import [org.gephi.statistics.plugin GraphDistance]))

(defn distance!
  "GraphModel -> GraphDistance"
  [gm directed?]
  (let [gd (GraphDistance.)]
    (.setDirected gd directed?)
    (.execute gd gm)
    gd))

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
