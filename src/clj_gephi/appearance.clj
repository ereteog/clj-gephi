(ns clj-gephi.appearance
  (import [org.openide.util Lookup])
  (import [org.gephi.appearance.api AppearanceController])
  (import [org.gephi.appearance.api AppearanceModel AppearanceModel$GraphFunction])
  (import [org.gephi.appearance.api Function])
  (import [org.gephi.appearance.plugin RankingElementColorTransformer])
  (import [org.gephi.appearance.plugin RankingNodeSizeTransformer])
  (import [java.awt Color])
  (require [clj-gephi.statistics :as stats])
  )

(def ac (.lookup (Lookup/getDefault) AppearanceController))

(defn appearance-model
  []
  (.getModel ac))

(defn degree-ranking
  [am graph]
  (.getNodeFunction am graph AppearanceModel$GraphFunction/NODE_DEGREE RankingElementColorTransformer)
  )

(defn centrality-ranking
  [am graph gm c-idx]
  (.getNodeFunction am graph
                    (stats/column gm c-idx)
                    RankingNodeSizeTransformer))

(defn betweenness-ranking
  [am graph gm]
  (centrality-ranking am graph gm stats/betweenness-idx))

(defn color-by!
  [ranking graph am colors positions]
  (let [dt (.getTransformer ranking)]
    (.setColors dt (into-array Color colors))
    (.setColorPositions dt (float-array positions))
    (.transform ac ranking))
  am)

(defn color-by-degree!
  [graph am colors positions]
  (color-by! (degree-ranking am graph)
            graph am colors positions))

(defn size-by!
  [ranking graph am min-size max-size]
  (let [ct (.getTransformer ranking)]
    (.setMinSize ct min-size)
    (.setMaxSize ct max-size)
    (.transform ac ranking))
  am)

(defn size-by-betweenness!
  [graph am gm min-size max-size]
  (size-by! (betweenness-ranking am graph gm)
           graph am min-size max-size))
