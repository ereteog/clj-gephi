(ns clj-gephi.appearance
  (import [org.openide.util Lookup])
  (import [org.gephi.appearance.api AppearanceController])
  (import [org.gephi.appearance.api AppearanceModel AppearanceModel$GraphFunction])
  (import [org.gephi.appearance.api Function])
  (import [org.gephi.appearance.plugin RankingElementColorTransformer])
  (import [org.gephi.appearance.plugin RankingNodeSizeTransformer])
  (import [org.gephi.appearance.api Function])
  (import [org.gephi.appearance.api Partition])
  (import [org.gephi.appearance.api PartitionFunction])
  (import [org.gephi.appearance.plugin PartitionElementColorTransformer])
  (import [org.gephi.appearance.plugin.palette Palette])
  (import [org.gephi.appearance.plugin.palette PaletteManager])
  (import [java.awt Color])
  (require [clj-gephi.statistics :as stats])
  )

(def ac (.lookup (Lookup/getDefault) AppearanceController))

(defn appearance-model
  []
  (.getModel ac))

(defn function
  "AppearanceModel -> Graph -> GraphFunction -> Transformer -> Function"
  [am graph function transformer]
  (.getNodeFunction am graph function transformer))

(defn degree-ranking
  "AppearanceModel -> Graph -> Function"
  [am graph]
  (function am graph AppearanceModel$GraphFunction/NODE_DEGREE RankingElementColorTransformer)
  )

(defn centrality-ranking
  "AppearanceModel -> Graph -> GraphModel -> String -> Function"
  [am graph gm c-idx]
  (.getNodeFunction am graph
                    (stats/column gm c-idx)
                    RankingNodeSizeTransformer))

(defn pagerank-ranking
  "AppearanceModel -> Graph -> GraphModel -> Function"
  [am graph gm]
  (centrality-ranking am graph gm stats/pagerank-idx))

(defn betweenness-ranking
  "AppearanceModel -> Graph -> GraphModel -> Function"
  [am graph gm]
  (centrality-ranking am graph gm stats/betweenness-idx))

(defn generate-palette
  "Integer -> Palette"
  [size]
  (-> (PaletteManager/getInstance)
      (.generatePalette size)))

(defn random-palette
  "Integer -> Palette"
  [size]
  (-> (.getInstance PaletteManager)
      (.randomPalette size)))

(defn partition-function
  "AppearanceModel -> Graph -> GraphModel -> String -> Function"
  [am graph gm property]
  (let [column (-> (.getNodeTable gm)
                   (.getColumn property))]
    (function am graph column PartitionElementColorTransformer)))

(defn color-by!
  [ranking am graph colors positions]
  (let [t (.getTransformer ranking)]
    (.setColors t (into-array Color colors))
    (.setColorPositions t (float-array positions))
    (.transform ac ranking))
  am)

(defn color-by-degree!
  [am graph colors positions]
  (color-by! (degree-ranking am graph)
             am graph colors positions))

(defn color-by-partition!
  ([am graph gm property palette-fn]
  (let [func (partition-function am graph gm property)
        partition (-> (cast PartitionFunction func)
                      .getPartition)
        palette (-> (.size partition)
                    palette-fn)]
    (->> (.getColors palette)
         (.setColors partition))
    (.transform ac func))
    am)
  ([am graph gm property]
   (color-by-partition! am graph gm property generate-palette)))

(defn color-by-modularity!
  ([am graph gm palette-fn]
   (color-by-partition! am graph gm stats/modularity-idx palette-fn))
  ([am graph gm]
   (color-by-partition! am graph gm stats/modularity-idx))
  )

(defn size-by!
  [ranking graph am min-size max-size]
  (let [ct (.getTransformer ranking)]
    (.setMinSize ct min-size)
    (.setMaxSize ct max-size)
    (.transform ac ranking))
  am)

(defn size-by-pagerank!
  [graph am gm min-size max-size]
  (size-by! (pagerank-ranking am graph gm)
            graph am min-size max-size))

(defn size-by-betweenness!
  [graph am gm min-size max-size]
  (size-by! (betweenness-ranking am graph gm)
           graph am min-size max-size))
