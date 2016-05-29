(ns clj-gephi.core
  (require [clj-gephi.project :as p])
  (require [clj-gephi.graph :as g])
  (require [clj-gephi.filters :as f])
  (require [clj-gephi.statistics :as stat])
  (require [clj-gephi.layout :as lay])
  (require [clj-gephi.importers :as imp])
  )

(defn test-gephi
  "https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/HeadlessSimple.java"
  []
  (def wp (p/new-project!))
  (def gm (g/graph-model))
  (imp/import-graph-file! wp "resources/netowrk-twitter-sarko-20130706.gexf" true)

  ;; print graph stats
  (let [graph (g/directed-graph gm)]
    (println "Nodes: " (g/node-count graph))
    (println "Edges: " (g/edge-count graph)))

  (let [d (stat/distance! gm true)]
    (println "diameter: " (stat/diameter d))
    (println "avg-distance: " (stat/avg-distance d)))

  (println "filtering nodes with degree less than 5")
  (f/filter-by-degree! gm (f/visible-view gm) 5)
  (let [graph (g/visible-graph gm)]
    (println "Filtered nodes: " (g/node-count graph))
    (println "Filtered edges: " (g/edge-count graph)))

  (let [d (stat/distance! gm true)]
    (println "diameter: " (stat/diameter d))
    (println "avg-distance: " (stat/avg-distance d)))

  (println "starting Yifan Hu Layout")
  (let [lay-opts {:step-displacement 1
                 :nb-loops 100
                 :optimal-distance 200}
        layout (lay/yifan-hu! gm lay-opts)]
    (println "completed layout")
    )

  )
