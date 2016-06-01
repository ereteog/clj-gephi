(ns clj-gephi.core
  (require [clj-gephi.project :as p])
  (require [clj-gephi.graph :as g])
  (require [clj-gephi.filters :as f])
  (require [clj-gephi.statistics :as stat])
  (require [clj-gephi.layout :as lay])
  (require [clj-gephi.io.import :as imp])
  (require [clj-gephi.io.export :as exp])
  (require [clj-gephi.appearance :as app])
  (require [clj-gephi.preview :as prev])
  (import [java.awt Color])
  (import [java.awt Font])
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

  (let [graph (g/visible-graph gm)
        am (app/appearance-model)]
    (app/color-by-degree! graph am
                         [(Color. 0xFEF0D9) (Color. 0xB3000)] [0 1])
    (app/size-by-betweenness! graph am gm 4 40))

  (let [pm (prev/preview-model)
        font (-> (prev/node-font-label pm)
                 (.deriveFont 8))]
    (prev/show-node-labels! pm true)
    (prev/edge-color! pm Color/PINK)
    (prev/edge-thickness! pm 0.1)
    (prev/node-font-label! pm font)
    )

  (exp/export-graph-file! "test.pdf")
  )
