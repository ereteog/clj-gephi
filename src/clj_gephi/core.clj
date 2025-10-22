(ns clj-gephi.core
  (:require [clj-gephi.project :as p]
            [clj-gephi.graph :as g]
            [clj-gephi.filters :as f]
            [clj-gephi.statistics :as stat]
            [clj-gephi.layout :as lay]
            [clj-gephi.io.import :as imp]
            [clj-gephi.io.export :as exp]
            [clj-gephi.appearance :as app]
            [clj-gephi.preview :as prev])
  (:import [java.awt Color Font]))

(defn test-gephi
  "Example workflow demonstrating Gephi toolkit usage.
  See: https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/HeadlessSimple.java"
  [filepath]
  (p/new-project!)
  (let [wp (p/current-workspace)
        gm (g/graph-model)]
    (imp/import-graph-file! wp filepath false)

    ;; Print graph stats
    (let [graph (g/undirected-graph gm)
          dist (stat/distance! gm false)
          pr (stat/pagerank! gm false)
          d (stat/degree! gm)
          mo (stat/modularity! gm)]
      (println "Nodes:" (g/node-count graph))
      (println "Edges:" (g/edge-count graph))
      (println "Average degree:" (stat/average-degree d))
      (println "Diameter:" (stat/diameter dist))
      (println "Avg distance:" (stat/avg-distance dist))
      (spit (str filepath "distance.html") (.getReport dist))
      (spit (str filepath "degree.html") (.getReport d))
      (spit (str filepath "pagerank.html") (.getReport pr))
      (spit (str filepath "modularity.html") (.getReport mo)))

    ;; Apply appearance
    (let [graph (g/visible-graph gm)
          am (app/appearance-model)]
      (app/color-by-modularity! am graph gm)
      (app/size-by-pagerank! graph am gm 3 30))

    ;; Configure preview
    (let [pm (prev/preview-model)
          font (-> (prev/node-font-label pm)
                   (.deriveFont (float 8)))]
      (prev/show-node-labels! pm true)
      (prev/edge-color! pm Color/LIGHT_GRAY)
      (prev/edge-thickness! pm 0.01)
      (prev/node-font-label! pm font))

    ;; Filter by degree
    (println "Filtering nodes with degree less than 4")
    (f/filter-by-degree! gm (f/visible-view gm) 4)
    (let [graph (g/visible-graph gm)]
      (println "Filtered nodes:" (g/node-count graph))
      (println "Filtered edges:" (g/edge-count graph)))

    ;; Apply layouts
    (let [force-opts {:attraction-strength 1.5
                      :repulsion-strength 0.5}
          yifan-opts {:step-displacement 1
                      :optimal-distance 200.0}
          label-adjust-opts {:adjust-by-size? true}
          noverlap-opts {:margin 10.0
                         :ratio 2.0}]
      (time
        (do
          (println "Starting Force Atlas Layout")
          (clojure.pprint/pprint force-opts)
          (lay/force-atlas! gm 50 force-opts)
          (println "Done")))
      (time
        (do
          (println "Starting Yifan Hu Layout")
          (clojure.pprint/pprint yifan-opts)
          (lay/yifan-hu! gm 50 yifan-opts)
          (println "Done")))
      (time
        (do
          (println "Starting Noverlap")
          (clojure.pprint/pprint noverlap-opts)
          (lay/noverlap! gm 50 noverlap-opts)))
      (time
        (do
          (println "Starting Label Adjust")
          (clojure.pprint/pprint label-adjust-opts)
          (lay/label-adjust! gm 500 label-adjust-opts)))
      (println "Done"))

    ;; Export results
    (exp/export-graph-file! (str filepath ".png"))
    (exp/export-graph-file! (str filepath ".svg"))
    (exp/export-graph-file! (str filepath ".pdf") wp)
    (exp/export-graph-file! (str filepath ".gexf") "gexf" wp true)))
