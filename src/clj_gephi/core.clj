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
  [filepath]
  (def wp (p/new-project!))
  (def gm (g/graph-model))
  ;;(imp/import-graph-file! wp "resources/netowrk-twitter-sarko-20130706.gexf" true)
  (imp/import-graph-file! wp filepath false)

  ;; print graph stats
  (let [graph (g/undirected-graph gm)]
    (println "Nodes: " (g/node-count graph))
    (println "Edges: " (g/edge-count graph)))

  (let [d (stat/distance! gm false)]
    (println "diameter: " (stat/diameter d))
    (println "avg-distance: " (stat/avg-distance d)))


  (let [
        dist (stat/distance! gm false)
        pr   (stat/pagerank! gm false)
        d    (stat/degree! gm)
        mo   (stat/modularity! gm)
        ]
    (println "average degree: " (stat/average-degree d))
    (println "diameter: "       (stat/diameter dist))
    (println "avg-distance: "   (stat/avg-distance dist))
    (spit (str filepath "distance.html"  )     (.getReport dist))
    (spit (str filepath "degree.html"    )     (.getReport d))
    (spit (str filepath "pagerank.html"  )     (.getReport pr))
    (spit (str filepath "modularity.html")     (.getReport mo))
    )

  (let [graph (g/visible-graph gm)
        am (app/appearance-model)]
                                        ;(app/color-by-degree! am graph [(Color. 0xFEF0D9) (Color. 0xB3000)] [0 1])
    (app/color-by-modularity! am graph gm)
    (app/size-by-pagerank! graph am gm 3 30)
    ;;(app/size-by-degree! graph am gm 8 20)
    ;;(app/size-by-betweenness! graph am gm 3 30)
    )

  (let [pm (prev/preview-model)
        font (-> (prev/node-font-label pm)
                 (.deriveFont 8))]
    (prev/show-node-labels! pm true)
    (prev/edge-color! pm Color/LIGHT_GRAY)
    (prev/edge-thickness! pm 0.01)
    (prev/node-font-label! pm font)
    )


  (println "filtering nodes with degree less than 4")
  (f/filter-by-degree! gm (f/visible-view gm) 4)
  (let [graph (g/visible-graph gm)]
    (println "Filtered nodes: " (g/node-count graph))
    (println "Filtered edges: " (g/edge-count graph)))

  (let [force-opts {:attraction-strength 1.5
                    :repulsion-strength  0.5}
        yifan-opts {:step-displacement 1
                    :optimal-distance 200.0}
        label-adjust-opts {:adjust-by-size? true}
        noverlap-opts {:margin 10.0
                           :ratio  2.0}]

        (time
         (do
           (println "starting Force Atlas Layout")
           (clojure.pprint/pprint force-opts)
           (lay/force-atlas! gm 50 force-opts)
           (println "done")))
        (time
         (do
           (println "starting Yifan Hu Layout")
           (clojure.pprint/pprint yifan-opts)
           (lay/yifan-hu! gm 50 yifan-opts)
           (println "done")))
        (time
         (do
           (println "starting Noverlap")
           (clojure.pprint/pprint noverlap-opts)
           (lay/noverlap! gm 50 noverlap-opts)))
        (time
         (do
             (println "starting Label Ajust")
             (clojure.pprint/pprint label-adjust-opts)
             (lay/label-adjust! gm 500 label-adjust-opts)))
        (println "done")

        ;; (println "starting Auto Layout")
        ;; (time
        ;;  (->> [[(lay/force-atlas force-opts) 0.5]
        ;;        [(lay/yifan-hu yifan-opts) 0.2]
        ;;        [(lay/label-adjust label-adjust-opts) 0.3]]
        ;;       (lay/auto-layout! gm 120000)))
        ;;  (println "done")
       )


  (exp/export-graph-file! (str filepath ".pdf"))
  (exp/export-graph-file! (str filepath ".png"))
  (exp/export-graph-file! (str filepath ".svg"))
  (exp/export-graph-file! (str filepath ".gexf"))
  )
